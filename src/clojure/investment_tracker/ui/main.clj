(ns investment-tracker.ui.main
  (:require [functional-vaadin.core :refer :all])
  (:import (com.vaadin.ui CustomComponent)
           (com.vaadin.navigator View)))

(defn- view-def []
  (vertical-layout
    (label "Main View")))

(defn create-view []
  (proxy [CustomComponent View] [(view-def)]
         (enter [event] (println "Entering main"))))
