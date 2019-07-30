(ns innovonto-solutionmap.events
  (:require [re-frame.core :as re-frame]
            [thi.ng.geom.viz.core :as geom]))


(re-frame/reg-event-db
  ::debug-print-db
  (fn [db _]
    (do
      (println db)
      db)))

(defn build-scale [values]
  (do
    (geom/linear-scale [(apply min values) (apply max values)]
                       [100 400])))

(defn re-scale [x-scale y-scale idea]
  (-> idea
      (assoc :x (x-scale (:x idea)))
      (assoc :y (y-scale (:y idea)))))

(defn transform-all-ideas [ideas]
  (let [x-scale (build-scale (map :x ideas))
        y-scale (build-scale (map :y ideas))]
    (map (partial re-scale x-scale y-scale) ideas)))

(re-frame/reg-event-db
  ::init-db-from-server-data
  (fn [db [_ [response]]]
    (assoc db :ideas (transform-all-ideas response))))

(re-frame/reg-event-db
  ::init-error
  (fn [db _]
    (assoc db :state "error")))

#_(defn load-data-from-server []
    (ajax/GET (str "/api/solutionmap-ideas")
              {
               :response-format :json
               :keywords?       true
               :handler         init-db-from-server-data
               :error-handler   init-error
               }))

(defn update-tooltip [target idea]
  (let [matrix (.translate (.getScreenCTM target) (.getAttribute target "cx") (.getAttribute target "cy"))]
    {
     :state   "shown"
     :left    (str (+ (.-pageXOffset js/window) (.-e matrix) -130) "px")
     :top     (str (+ (.-pageYOffset js/window) (.-f matrix) 10) "px")
     :content {:image (:thumbnailPath idea) :title (:title idea) :description (:description idea)}}))

(re-frame/reg-event-db
  ::show-tooltip
  (fn [db [_ target idea]]
    (do
      (println (str "Now Showing:" idea " in tooltip"))
      (assoc db :tooltip (update-tooltip target idea))
      )))

(re-frame/reg-event-db
  ::hide-tooltip
  (fn [db _]
    (update-in db [:tooltip] assoc :state "hidden")))
