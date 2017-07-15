(ns investment-tracker.system
  (:require [datomic.api :as d]
            [investment-tracker.config :as c]
            [investment-tracker.server :refer :all])
  )

(defn make-system [conf]
  (assoc
    {:db {:uri (:db-uri conf)}}
    :server (if-let [rb (:resource-base conf)]
              (jetty-server "investment_tracker.ui.UI" rb))))

(defn load-custodians [s]
  (let [db (d/db (get-in s [:db :conn]))
        custs (apply d/pull db "[*]"
                (d/q '[:find [?c ...] :where [?c :custodian/name]] db))]
    (assoc s :custodians custs)))

(defn open-db [s]
  (assoc-in s [:db :conn] (d/connect (get-in s [:db :uri]))))

(defn close-db [s]
  (dissoc s :db :conn))

(defn start-server [s]
  (when-let [server (:server s)]
    (println "Starting server")
    (.start server))
  s)

(defn stop-server [s]
  (when-let [server (:server s)]
    (println "Stopping server" )
    (.stop server)))

(defn start-system [s]
  (-> s
    (open-db)
    (load-custodians)
    (start-server))
  )

(defn stop-system [s]
  (-> s
    (stop-server)
    (close-db)))

(def system nil)

(defn init
  ([conf]
   (alter-var-root #'system (fn [_] (make-system conf))))
  ([] (init c/dev-base)))

(defn start []
  (alter-var-root #'system start-system))

(defn stop []
  (alter-var-root #'system (fn [s] (when s (stop-system s)))))

(defn system-assoc [keys value]
  (alter-var-root #'system (fn [s] (assoc-in s keys value)))
  )

(defn system-dissoc [keys]
  (alter-var-root #'system (fn [s] (apply dissoc s keys))))

(defn go
  ([conf] (init conf) (start))
  ([] (go c/dev-base)))


