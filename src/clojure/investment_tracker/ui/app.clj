(ns investment-tracker.ui.app
  (:use functional-vaadin.core
        functional-vaadin.event-handling
        investment-tracker.ui.menu-bar)
  (:import (com.vaadin.ui CustomComponent)
           (com.vaadin.navigator View)))


(defn- clear-user [ui]
  (.setAttribute (.getSession ui) "user" nil))

(defn- view-def []
  (vertical-layout
    (my-menu-bar)))

(defn wire-up
  "Connect all events handlers"
  [ui]
  )

(defn create-view
  "Create the application view" []
  (doto
    (proxy [CustomComponent View] [(view-def)]
     (enter [event]))
    (.setSizeFull)
    ))
