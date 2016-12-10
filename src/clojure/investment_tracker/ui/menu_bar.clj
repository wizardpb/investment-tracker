(ns investment-tracker.ui.menu-bar
  (:require [investment-tracker.authentication :as auth])
  (:use functional-vaadin.core
        functional-vaadin.event-handling)
  (:import (com.vaadin.ui UI Alignment)))

(defn- clear-user [ui]
  (.setAttribute (.getSession ui) "user" nil))

(defn my-menu-bar[]
  (menu-bar {:width "100%"}
    (menu-item "Import" (fn [_]))
    (menu-item "Admin" (fn [_]))
    (menu-item "Logout"
      (fn [item]
        (let [ui (UI/getCurrent)]
          (clear-user ui)
          (auth/logout)
          (.showLoginView ui))))))
