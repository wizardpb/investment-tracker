(ns investment-tracker.core
  (:require
    ;[clojure.string :as str]
    ;[clojure.data.csv :as csv]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :refer [response content-type]]
            [ring.util.request :refer [path-info]]
            [ring.middleware.logger :refer [wrap-with-logger]]
    ;[ring.adapter.jetty :as jetty]
            [compojure.route :as route]
    ;; [cheshire.core :as json]
            )
  (:use compojure.core
        clojure.java.io
        clojure.pprint)
  )

(defroutes
  app-routes
  ;(GET <route> [:as {{currentPortfolio :currentPortfolio} :session}]
  ;     <action>)
  (route/files "/"))

(def app
  (->
    (wrap-session app-routes)
    (wrap-params)
    (wrap-json-response)
    (wrap-with-logger)
    ))