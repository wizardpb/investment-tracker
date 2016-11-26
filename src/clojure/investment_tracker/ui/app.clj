(ns investment-tracker.ui.app
  (:require [functional-vaadin.core :refer :all])
  (:import (com.vaadin.ui CustomComponent)
           (com.vaadin.navigator View)))

(defn- view-def []
  (vertical-layout
    (label "Main View")))

(defn create-view []
  (doto
    (proxy [CustomComponent View] [(view-def)]
     (enter [event]
       ;(println "Entering main")
       ))
    (.setSizeFull)
    ))
