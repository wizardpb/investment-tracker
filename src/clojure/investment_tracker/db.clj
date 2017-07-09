(ns investment-tracker.db
  "Functions to interface with the database"
  (:require [datomic.api :as d]
            [investment-tracker.system :as sys]
            [investment-tracker.authentication :as auth]))

(defprotocol Updateable
  (ref-attribute [this rec]
    "Return a (reverse) attribute that refers to the rec type on this")
  (update-map [this] [this ref] [this keys ref]
    "Return an map that saves the given keys of this record plus an attribute to add it to the container. If no keys
    are given, the value of update-keys is used. An optional ref adds an attribute to connect this to the
    ref holder")
  )

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

(defn db-key [rec key]
  (let [ns (.toLowerCase (.getSimpleName (class rec)))]
    (keyword (str (name ns) "/" (name key)))))

(defn entity-map
  "Return a map of values that can be transacted to save all values in this record. Adds a tempId if the record is new,
  filters any nil attributes, and adds an entry to add it to ref if present"
  [rec keys ref]
  (let [entity-id (if-let [id (:id rec)] id "tempId")]
    (merge
     (if ref (ref-attribute ref rec) {})
     (into {:db/id entity-id}
       (map
         #(vector (db-key rec %) (% rec))
         (filter #(get rec %) keys))))))

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