(ns investment-tracker.position
  (:require [investment-tracker.db :as db])
  )

(defn- entity-map [p keys ref]
  (merge {:position/security (:id (:security p))} (db/entity-map p keys ref)))

(defrecord Position [security lots]
  db/Updateable
  (ref-attribute [this rec])
  (update-map [this keys ref]
    (entity-map this keys ref))
  (update-map  [this ref]
    (entity-map this [] ref))
  (update-map [this]
    (entity-map this [] nil)))

(defn db->Position [entity-map]
  (db/make-record map->Position entity-map))



