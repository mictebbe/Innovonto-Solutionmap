(ns ^:figwheel-hooks innovonto-solutionmap.core
  (:require
    [goog.dom :as gdom]
    [innovonto-solutionmap.views :as views]
    [innovonto-solutionmap.db :as db]
    [thi.ng.geom.viz.core :as geom]
    [ajax.core :as ajax]
    [reagent.core :as reagent :refer [atom]]))


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

(defn init-db-from-server-data [server-data]
  (do
    (reset! db/app-state (assoc @db/app-state :ideas (transform-all-ideas server-data)))))

(defn init-error []
  (reset! db/app-state (assoc @db/app-state :state "error")))


(defn load-data-from-server []
  (ajax/GET (str "/api/solutionmap-ideas")
            {
             :response-format :json
             :keywords? true
             :handler init-db-from-server-data
             :error-handler init-error
             }))

(defn get-app-element []
  (gdom/getElement "app"))

;;TODO init-database
(defn mount [el]
  (reagent/render-component [views/solutionmap-app] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
