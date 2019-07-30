(ns innovonto-solutionmap.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::app-state
  (fn [db _]
    db))

