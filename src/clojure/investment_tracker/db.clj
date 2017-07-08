(ns investment-tracker.db
  "Functions to interface with the database"
  (:require [datomic.api :as d]
            [investment-tracker.system :as sys]
            [investment-tracker.authentication :as auth]))

(defn make-txn [attrs]
  [(merge {:db/id "datomic.tx"} attrs)])

(defn make-txn-dated [date attrs]
  (make-txn {:db/txInstant date}))

(defn ->Txn
  ([date type actor desc contents]
   (let [details {:db.tx/type        type
                  :db.tx/actor-id    (if actor actor "")
                  :db.tx/description desc}]
     (vec
       (concat
        (if date
          (make-txn-dated date details)
          (make-txn details))
        contents))))
  ([actor type desc contents] (->Txn nil actor type desc contents))
  ([desc contents] (->Txn nil :UserTxn (auth/current-user-id) desc contents))
  ([contents] (->Txn nil :UserTxn (auth/current-user-id) "" contents)))

(defn db-key [ns key]
  (keyword (str (name ns) "/" (name key))))

(defn db-ns [rec]
  (.toLowerCase (.getSimpleName (class rec))))

(defn ->Entity [rec]
  (into {:db/id "tempId"}
    (map #(vector (db-key (db-ns rec) %) (% rec))
      (filter #(get rec %) (keys rec)))))

(defn getdb []
  (d/db (get-in sys/system [:db :conn])))

(defn get-user [user-id]
  (d/entity (getdb) [:user/id user-id]))

(defn make-record [rec-fn entity]
  (rec-fn (into {} (map (fn [[k v]] [(keyword (name k)) v]) entity))))

(defn get-security [ident]
  (d/pull (getdb) "[*]" ident))

(defn get-account [custodian-id]
  (d/pull (getdb) "[*]" [:account/custodian-id custodian-id]))

(defn get-all-accounts []
  (flatten (d/q "[:find (pull ?e [*]) :where [?e :account/custodian-id]]" (getdb))))