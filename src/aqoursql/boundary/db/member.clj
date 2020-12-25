(ns aqoursql.boundary.db.member
  (:require
   [aqoursql.boundary.db.artist :as artist]
   [aqoursql.boundary.db.core :as db]
   [aqoursql.boundary.db.organization :as organization]
   [clojure.spec.alpha :as s]
   [duct.database.sql]
   [honeysql.core :as sql]
   [honeysql.helpers :refer [merge-join merge-order-by merge-select merge-where]]))

(s/def ::id nat-int?)
(s/def ::name string?)
(s/def ::organization_id ::organization/id)

(s/def ::organization_name ::organization/name)
(s/def ::artist_id ::artist/id)
(s/def ::artist_ids (s/coll-of ::artist/id))

(s/def ::member
  (s/keys :req-un [::id
                   ::name
                   ::organization_id]
          :opt-un [::organization_name
                   ::artist_id]))

(s/fdef find-member-by-id
  :args (s/cat :db ::db/db
               :id ::id)
  :ret (s/nilable ::member))

(s/fdef find-members
  :args (s/cat :db ::db/db
               :tx-data (s/nilable (s/keys :opt-un [::name
                                                    ::organization_name
                                                    ::artist_id
                                                    ::artist_ids])))
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

(defn where-=-artist-id [sql artist_id]
  (-> sql
      (merge-join [:artist_member :am]
                  [:= :m.id :am.member_id])
      (merge-where [:= :am.artist_id artist_id])))

(defn where-in-artist-id [sql artist_ids]
  (-> sql
      (merge-select [:am.artist_id :artist_id])
      (merge-join [:artist_member :am]
                  [:= :m.id :am.member_id])
      (merge-where [:in :am.artist_id artist_ids])))

(extend-protocol Member
  duct.database.sql.Boundary
  (find-member-by-id [db id]
    (db/select-first db (merge-where sql-member-with-organization [:= :m.id id])))
  (find-members [db {:keys [name organization_name artist_id artist_ids]}]
    (db/select db (cond-> sql-member-with-organization
                    name (merge-where [:like :m.name (str \% name \%)])
                    organization_name (merge-where [:like
                                                    :o.name
                                                    (str \% organization_name \%)])
                    artist_id (where-=-artist-id artist_id)
                    artist_ids (where-in-artist-id artist_ids)
                    true (merge-order-by [:m.id :asc])))))
