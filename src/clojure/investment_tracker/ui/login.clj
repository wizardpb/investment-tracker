(ns investment-tracker.ui.login
  (:require [functional-vaadin.core :refer :all])
  (:import (com.vaadin.ui CustomComponent)
           (com.vaadin.navigator View)))

(defn- do-login [source event username password]
  (println "Login"))

(defn- view-def []
  (vertical-layout
    (login-form {:id :login.form} do-login)
    (label {:id :login.error-msg})))

(defn create-view []
  (proxy [CustomComponent View] [(view-def)]
    (enter [event] (println "Entering login"))))
