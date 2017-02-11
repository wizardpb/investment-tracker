(ns investment-tracker.system
  (:require [datomic.api :as d]
            [investment-tracker.config :as conf]
            [investment-tracker.server :refer :all])
  )

(defn- make-system [resource-base]
  {:db     {:uri (:db-uri conf/settings)}
   :server (jetty-server "investment_tracker.ui.UI" resource-base)})

(defn- start-server [s]
  (println "Starting system")
  (.start (:server s))
  s)

(defn- stop-server [s]
  (println "Stopping system" )
  (.stop (:server s)))

(defn- start-system [s]
  (-> s
    (assoc-in [:db :conn] (d/connect (get-in s [:db :uri])))
    (start-server))
  )

(defn- stop-system [s]
  (-> s
    (stop-server)
    (dissoc :db :conn)))

(def system nil)

(defn init []
  (alter-var-root #'system (fn [_] (make-system "resources/public"))))

(defn start []
  (alter-var-root #'system start-system))

(defn stop []
  (alter-var-root #'system (fn [s] (when s (stop-system s)))))

(defn system-assoc [keys value]
  (alter-var-root #'system (fn [s] (assoc-in s keys value)))
  )

(defn system-dissoc [keys]
  (alter-var-root #'system (fn [s] (apply dissoc s keys))))

(defn go []
  (init)
  (start))


