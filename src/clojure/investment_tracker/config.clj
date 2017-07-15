(ns investment-tracker.config
  "Configuration information")

(def dev-base
  {:db-uri "datomic:dev://localhost:4334/investment-tracker"
   :resource-base "resources/public"})

(def test-base
  {:db-uri "datomic:dev://localhost:4334/investment-tracker-test"
   ;:resource-base "resources/public"
   })
