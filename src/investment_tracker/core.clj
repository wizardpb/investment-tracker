(ns investment-tracker.core
  (:require
    [ring.util.response :refer [response content-type]]
    [ring.util.request :refer [path-info]]
    [ring.middleware.json :refer [wrap-json-response]]
    [ring.middleware.session :refer [wrap-session]]
    [ring.middleware.params :refer [wrap-params]]
    ;[ring.middleware.logger :refer [wrap-with-logger]]
    [compojure.route :as route]
    )
  (:use compojure.core)
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
    ))