(ns investment-tracker.server
  (:import (com.vaadin.server VaadinServlet)
           (org.eclipse.jetty.servlet ServletContextHandler)
           (org.eclipse.jetty.server Server)))

(defn jetty-server [ui-name resource-base]
  (doto (Server. 8080)
    (.setHandler
      (doto (ServletContextHandler. ServletContextHandler/SESSIONS)
        (.setContextPath "/")
        (.setInitParameter "UI" ui-name)
        (.setInitParameter "legacyPropertyToString" "true")
        (.setResourceBase resource-base)
        (.addServlet VaadinServlet "/*")))))