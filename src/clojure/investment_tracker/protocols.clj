(ns investment-tracker.protocols)

(defprotocol Storeable
  (associate [this rec]
    "Associate rec with this record. The type of rec determines the reverse attribte to use")
  (update-db [this] [this keys]
    "Return an map that saves the given keys of this record plus an attribute to add it to the container. If no keys
    are given, all non-nil keys are used. If a new entity is created, the new entity id is saved in the record as :id")
  )
