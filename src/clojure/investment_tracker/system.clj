(ns investment-tracker.system
  (:require [datomic.api :as d]
            [investment-tracker.config :as conf]
            [investment-tracker.server :refer :all])
  )

(defn make-system [resource-base]
  {:db     {:uri (:db-uri conf/settings)}
   :server (jetty-server "investment_tracker.ui.UI" resource-base)})

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
  (println "Starting system")
  ;(.start (:server s))
  s)

(defn stop-server [s]
  (println "Stopping system" )
  (.stop (:server s)))

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


