(ns investment-tracker.ui.UI
  (:require [functional-vaadin.core :refer :all]
            [investment-tracker.ui.login :as login]
            [investment-tracker.ui.app :as app]
            )
  (:gen-class :name ^{com.vaadin.annotations.Theme "valo"} investment_tracker.ui.UI
              :extends com.vaadin.ui.UI
              :main false
              :methods [
                        [getAppState [java.util.List] Object]
                        [setAppState [java.util.List Object] void]
                        [getAppNavigator [] com.vaadin.navigator.Navigator]]
              :init init-state
              :state _appState)
  )

(defn- wire-up
  "Wire up the event handlers for this UI"
  [ui]
  (login/wire-up ui))

(defn -init-state []
  [[] (atom {})])

(defn -getAppState [this key-vec]
  (get-in @(.-_appState this) key-vec))

(defn -setAppState [this key-vec value]
  (swap! (.-_appState this) #(assoc-in % key-vec value)))

(defn -getAppNavigator [this]
  (.getAppState this [:navigator]))

(defn -init [this request]
  (let [nav (com.vaadin.navigator.Navigator. this this)]
    (defui this
      (do
        (.addView nav "" (login/create-view))
        (.addView nav "main" (app/create-view))
        (panel)))
    (wire-up this)
    (.setAppState this [:navigator] nav)
    (.navigateTo nav (login/get-initial-view))
    )
  )
