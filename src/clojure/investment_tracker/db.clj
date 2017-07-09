(ns investment-tracker.db
  "Functions to interface with the database"
  (:require [datomic.api :as d]
            [investment-tracker.system :as sys]
            [investment-tracker.authentication :as auth])
  (:use investment-tracker.protocols))

(defn get-connection []
  (get-in sys/system [:db :conn]))

(defn getdb []
  (d/db (get-connection)))

(defn make-txn [date attrs]
  [(merge (if date {:db/id "datomic.tx" :db/txInstant date} {:db/id "datomic.tx"}) attrs)])

(defn ->Txn
  ([date type actor desc contents]
   (let [details {:db.tx/type        type
                  :db.tx/actor-id    (if actor actor "")
                  :db.tx/description desc}]
     (vec (concat (make-txn date details) contents))))
  ([actor type desc contents] (->Txn nil actor type desc contents))
  ([desc contents] (->Txn nil :UserTxn (auth/current-user-id) desc contents))
  ([contents] (->Txn nil :UserTxn (auth/current-user-id) "" contents)))

(defn transact [contents]
  (let [{:keys [tempids]} @(d/transact (get-connection) (->Txn contents))]
    (tempids "tempId")))

(defn db-key [rec key]
  (let [ns (.toLowerCase (.getSimpleName (class rec)))]
    (keyword (str (name ns) "/" (name key)))))

(defn entity-map
  "Return a map of values that can be transacted to save all values in this record. Adds a tempId if the record is new
  and filters any nil attributes"
  [rec keys]
  (let [entity-id (if-let [id (:id rec)] id "tempId")]
    (into {:db/id entity-id}
      (map
        #(vector (db-key rec %) (% rec))
        (filter #(get rec %) keys)))))

(defn update-record [rec keys]
  (if-let [new-id (transact [(entity-map rec keys)])]
    (assoc rec :id new-id)
    rec))

(defn get-user [user-id]
  (d/entity (getdb) [:user/id user-id]))

(defn make-record [rec-fn entity]
  (rec-fn (into {} (map (fn [[k v]] [(keyword (name k)) v]) entity))))

(defn get-entity [ident]
  (d/pull (getdb) "[*]" ident))

(defn get-account [custodian-id]
  (d/pull (getdb) "[*]" [:account/custodian-id custodian-id]))

(defn get-all-accounts []
  (flatten (d/q "[:find (pull ?e [*]) :where [?e :account/custodian-id]]" (getdb))))