(ns investment-tracker.authentication
  "All authentication and authorization functions"
  (:require [clojurewerkz.scrypt.core :as crypt]
            [buddy.core.nonce :as nonce]
            [buddy.core.codecs :as codec]
            [investment-tracker.db :refer :all]
            )
  (:import (javax.security.auth.login AccountNotFoundException)))

(defn user-logged-in? [ui]
  (.getAttribute (.getSession ui) "user"))

(defn validate-user [username credential]
  (if-let [user (get-user username)]
    (and (crypt/verify credential (:user/credentials user)) user)
    (throw (AccountNotFoundException. username))))

(defn login-user [user ui]
  (.setAttribute (.getSession ui) "user" user))

(defn clear-user [ui]
  (.setAttribute (.getSession ui) "user" nil))