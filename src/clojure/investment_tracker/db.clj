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
  "Form a transaction record that includes provenance information about the writer"
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
    tempids))

(defn db-ns [rec]
  (.toLowerCase (.getSimpleName (class rec))))

(defn db-key [rec key]
  (let [ns (db-ns rec)]
    (keyword (str ns "/" (name key)))))

(defn value-type
  "Return the db value type for the record key 'key' of record 'rec'"
  [rec key]
  (let [query '[:find ?i ?ti
                :where
                [?e :db/ident ?i]
                [?e :db/valueType ?t]
                [?t :db/ident ?ti]
                ]
        result (d/q query (getdb))
        attr-types (into {} (filter
                      #(= (namespace (first %)) (db-ns rec))
                      result))
        res (attr-types (db-key rec key))]
    res))

(declare entity-map)

(defmulti attr-value value-type)

(defmethod attr-value :db.type/ref [rec key]
  (if-let [key-id (:id (get rec key))]
    [(db-key rec key) key-id]
    ))

(defmethod attr-value :default [rec key ]
  [(db-key rec key) (get rec key)])

(defn entity-map
  "Return a map of values that can be transacted to save all values in this record. Adds a tempId if the record is new
  and filters any nil attributes."
  [rec keys]
  (let [tid (if-let [id (:id rec)] id (str (db-ns rec) ".id"))]
    (into {:db/id tid} (map #(attr-value rec %) (filter #(get rec %) keys)))
    )
  )

(defn update-record
  [rec keys]
  (let [ent (entity-map rec keys)
        tempIds (transact [ent])]
    (if (seq tempIds)
      (assoc rec :id (get tempIds (:db/id ent)))
      rec)))

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