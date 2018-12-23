(ns aqoursql.boundary.db.member
  (:require [aqoursql.boundary.db.core :as db]
            [aqoursql.boundary.db.organization :as organization]
            [clojure.spec.alpha :as s]
            [duct.database.sql]
            [honeysql.core :as sql]
            [honeysql.helpers :refer [merge-order-by merge-where]]))

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

(s/fdef find-members
  :args (s/cat :db ::db/db
               :tx-data (s/nilable (s/keys :opt-un [::name
                                                    ::organization_name])))
  :ret (s/coll-of ::member))

(defprotocol Member
  (find-member-by-id [db id])
  (find-members [db tx-data]))

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
    (db/select-first db (merge-where sql-member-with-organization [:= :m.id id])))
  (find-members [db {:keys [name organization_name]}]
    (db/select db (cond-> sql-member-with-organization
                    name (merge-where [:like :m.name (str \% name \%)])
                    organization_name (merge-where [:like
                                                    :o.name
                                                    (str \% organization_name \%)])
                    true (merge-order-by [:m.id :asc])))))
