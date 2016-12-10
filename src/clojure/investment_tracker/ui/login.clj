(ns investment-tracker.ui.login
  (:require [investment-tracker.authentication :as auth]
            [investment-tracker.db :as db])
  (:use functional-vaadin.core
        functional-vaadin.event-handling)
  (:import (com.vaadin.ui CustomComponent Alignment UI)
           (com.vaadin.navigator View)
           (com.vaadin.sass.internal.parser.function AlphaFunctionGenerator)
           (javax.security.auth.login AccountNotFoundException)))

(defn- login-failed [ui]
  (.setValue (componentNamed ::error-msg ui) "Login failed: incorrect username or password"))

(defn- clear-error-msg [ui]
  (.setValue (componentNamed ::error-msg ui) ""))

(defn- user-logged-in? [ui]
  (.getAttribute (.getSession ui) "user"))

(defn- login-user [user ui]
  (.setAttribute (.getSession ui) "user" user)
  (auth/login user))

(defn- do-login [source event username password]
  (let [ui (.getUI source)]
    (try
      (if-let [user (auth/validate-user (db/get-user username) username password)]
       (do
         (clear-error-msg ui)
         (login-user user ui)
         (.showAppView ui))
       (login-failed ui))
      (catch AccountNotFoundException e
        (login-failed ui))))
  )

(defn- view-def
  "Return the definition for the login view" []
  (vertical-layout {:sizeFull nil}
    (vertical-layout {:sizeUndefined nil :alignment Alignment/MIDDLE_CENTER}
      (login-form {:id ::form} do-login)
      (label {:id ::error-msg :width "400px" }))))

(defn wire-up
  "Connect all events handlers"
  [ui])

(defn get-initial-view
  "Return the name of the initial view of the app. Depends on if the user is logged in" []
  (if (user-logged-in? (UI/getCurrent))
    "main"
    ""))

(defn create-view
  "Create the login view" []
  (doto
    (proxy [CustomComponent View] [(view-def)]
     (enter [event]))
    (.setSizeFull)))
