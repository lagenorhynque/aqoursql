(ns aqoursql.boundary.db.member
  (:require [aqoursql.boundary.db.core :as db]
            [aqoursql.boundary.db.organization :as organization]
            [clojure.spec.alpha :as s]
            [duct.database.sql]
            [honeysql.core :as sql]
            [honeysql.helpers :refer [merge-where]]))

(s/def ::id nat-int?)
(s/def ::name string?)
(s/def ::organization_id ::organization/id)

(s/def ::organization_name ::organization/name)

(s/def ::member
  (s/keys :req-un [::id
                   ::name
                   ::organization_id]
          :opt-un [::organization_name]))

(s/fdef find-member-by-id
  :args (s/cat :db ::db/db
               :id ::id)
  :ret (s/nilable ::member))

(defprotocol Member
  (find-member-by-id [db id]))

(def sql-member-with-organization
  (sql/build
   :select [:m.*
            [:o.name :organization_name]]
   :from [[:member :m]]
   :join [[:organization :o]
          [:= :m.organization_id :o.id]]))

(extend-protocol Member
  duct.database.sql.Boundary
  (find-member-by-id [db id]
    (db/select-first db (merge-where sql-member-with-organization [:= :m.id id]))))
