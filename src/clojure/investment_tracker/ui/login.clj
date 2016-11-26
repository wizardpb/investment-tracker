(ns investment-tracker.ui.login
  (:require [functional-vaadin.core :refer :all]
            [functional-vaadin.utils :refer :all])
  (:import (com.vaadin.ui CustomComponent Alignment)
           (com.vaadin.navigator View)))

(defn- validate-user [username password]
  (= username "paul"))

(defn- do-login [source event username password]
  (println "Login: " source event username password)
  (let [ui (.getUI source)]
    (if (validate-user username password)
     (.navigateTo (.getAppNavigator ui) "main")
     (.setValue (componentNamed :login.error-msg ui) "Login failed: incorrect username or password")))
  )

(defn- view-def []
  (vertical-layout {:sizeFull []}
    (vertical-layout {:sizeUndefined [] :alignment Alignment/MIDDLE_CENTER}
     (login-form {:id :login.form} do-login)
     (label {:id :login.error-msg}))))

(defn create-view []
  (doto
    (proxy [CustomComponent View] [(view-def)]
     (enter [event]
       ;(println "Entering login")
       ))
    (.setSizeFull)))
