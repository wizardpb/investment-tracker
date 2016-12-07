(ns investment-tracker.ui.app
  (:require [functional-vaadin.core :refer :all])
  (:import (com.vaadin.ui CustomComponent)
           (com.vaadin.navigator View)))

(defn- view-def []
  (vertical-layout {:spacing true :margin true}
    (button {:id ::logout :caption "Logout"})))

(defn wire-up
  "Wire up all application event handlers"
  [ui]
  )

(defn create-view
  "Create the application view" []
  (doto
    (proxy [CustomComponent View] [(view-def)]
     (enter [event]))
    (.setSizeFull)
    ))
