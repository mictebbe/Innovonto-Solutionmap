(ns innovonto-solutionmap.views
  (:require [innovonto-solutionmap.db :as db]
            [reagent.core :as reagent]))


(defn hide-tooltip! []
  (swap! db/app-state update-in [:tooltip] assoc :state "hidden"))

(defn update-tooltip [db event idea]
  (let [target (.-target event)
        matrix (.translate (.getScreenCTM target) (.getAttribute target "cx") (.getAttribute target "cy"))]
    (println "swapping db")
    (assoc db :tooltip
              {
               :state   "shown"
               :left    (str (+ (.-pageXOffset js/window) (.-e matrix) -130) "px")
               :top     (str (+ (.-pageYOffset js/window) (.-f matrix) 10) "px")
               :content {:image (:thumbnailPath idea) :title (:title idea) :description (:description idea)}})))

(defn show-tooltip! [event idea]
  (do
    (println (str "Now Showing:" idea " in tooltip"))
    (let [new-db (update-tooltip @db/app-state event idea)]
      (println (str "New DB is: " new-db))
      (reset! db/app-state new-db))

    (println (str "Hello!!!!"))
    (reagent/force-update-all)))


;;TODO this hides the tooltip as soon as the mouse leaves the "circle" element. A better behaviour would be: include the tooltip div in the mouse-hover area
;;TODO Desired :on-click behaviour: leaves the tooltip open until the user clicks somewehere else ('pinned')
(defn idea-circle [idea]
  [:circle {:key           (:index idea)
            :cx            (:x idea)
            :cy            (:y idea)
            :r             5
            :class         "idea-circle"
            :on-mouse-over #(show-tooltip! % idea)
            :on-click      #(swap! db/app-state update-in [:clicks] inc)}])
;;:on-mouse-out  hide-tooltip!}])


;;TOOLTIP STUFF
;[idea-detail-component {:title "Rescue Window" :description "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed."}]
(defn tooltip [config]
  (do
    (println (str "now rerendering tooltip!" config))
    [:div.idea-detail-container {:class (:state config) :style {:top (:top config) :left (:left config)}}
     [:img {:src (:image (:content config))}]
     [:h2 (:title (:content config))]
     [:p (:description (:content config))]]))


(defn solutionmap-app []
  [:div
   [tooltip (:tooltip @db/app-state)]
   [:svg.solution-map {:view-box "0 0 500 500"}
    (map idea-circle (:ideas @db/app-state))]])


