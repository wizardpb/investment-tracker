(ns investment-tracker.ui.login
  (:use functional-vaadin.core
        functional-vaadin.utils
        functional-vaadin.event-handling
        investment-tracker.authentication)
  (:import (com.vaadin.ui CustomComponent Alignment UI)
           (com.vaadin.navigator View)
           (com.vaadin.sass.internal.parser.function AlphaFunctionGenerator)))

(defn- do-login [source event username password]
  (println "Login: " source event username password)
  (let [ui (.getUI source)]
    (if-let [user (validate-user username password)]
      (do
        (login-user user ui)
        (.navigateTo (.getAppNavigator ui) "main"))
      (.setValue (componentNamed :login.error-msg ui) "Login failed: incorrect username or password")))
  )


(defn- view-def
  "Return the definition for the login view" []
  (vertical-layout {:sizeFull nil}
    (vertical-layout {:sizeUndefined nil :alignment Alignment/MIDDLE_CENTER}
      (login-form {:id :login.form} do-login)
      (label {:id :login.error-msg :width "400px" }))))

(defn wire-up
  "Connect all events handlers"
  [ui]
  (onClick (componentNamed :app/logout ui)
    (fn [src event _]
      (clear-user ui)
      (.navigateTo (.getAppNavigator ui) ""))))

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
