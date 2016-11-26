(ns investment-tracker.ui.UI
  (:require [functional-vaadin.core :refer :all]
            [investment-tracker.ui.login :as login]
            [investment-tracker.ui.main :as main]
            )
  (:import (com.vaadin.navigator Navigator))
  (:gen-class :name ^{com.vaadin.annotations.Theme "valo"} investment_tracker.ui.UI
              :extends com.vaadin.ui.UI
              :main false
              :methods [ [getAppState [java.util.List] Object] [setAppState [java.util.List Object] void]]
              :init init-state
              :state _appState)
  )

(defn -init-state []
  [[] (atom {})])

(defn -getAppState [this key-vec]
  (get-in @(.-_appState this) key-vec))

(defn -setAppState [this key-vec value]
  (swap! (.-_appState this) #(assoc-in % key-vec value)))

(defn -init [this request]
  (let [nav (Navigator. this this)]
    (defui this
      (do
        (.addView nav "" (login/create-view))
        (.addView nav "main" (main/create-view))
        (panel)))
    (.setAppState this [:navigator] nav)
    (.navigateTo nav "")
    )
  )
