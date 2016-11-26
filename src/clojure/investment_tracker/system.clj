(ns investment-tracker.system
  (:require [investment-tracker.dbinit.core :as dbi]
            [datomic.api :as d])
  (:import (com.vaadin.server VaadinServlet)
           (org.eclipse.jetty.servlet ServletContextHandler)
           (org.eclipse.jetty.server Server)))

(defn jetty-server [ui-name resource-base]
  (let [server (Server. 8080)
        ^ServletContextHandler context (ServletContextHandler. ServletContextHandler/SESSIONS)]
    (.setContextPath context "/")
    (.setInitParameter context "UI" ui-name)
    (.setInitParameter context "legacyPropertyToString" "true")
    (.setResourceBase context resource-base)
    (.setHandler server context)
    (.addServlet context VaadinServlet "/*")
    server
    )
  )

(defn system [resource-base]
  {:db     {:uri dbi/uri}
   :server (jetty-server "investment_tracker.ui.UI" resource-base)})

(defn- start-server [s]
  (println "Starting system")
  (.start (:server s))
  s)

(defn- stop-server [s]
  (println "Stopping system" )
  (.stop (:server s)))

(defn start [s]
  (-> s
    ;(assoc-in [:db :conn] (d/connect (get-in s [:db :uri])))
    (start-server))
  )

(defn stop [s]
  (-> s
    (stop-server)
    (dissoc :db :conn)))
