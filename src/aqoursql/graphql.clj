(ns aqoursql.graphql
  (:require [aqoursql.resolver.members :as members]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.walmartlabs.lacinia.pedestal :as lacinia]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :as util]
            [integrant.core :as ig]))

(def resolver-map
  {:query/member-by-id members/fetch-member-by-id
   :query/members members/list-members
   ;; TODO: implement list-artist-members resolver
   :Artist/members (constantly [])
   ;; TODO: implement fetch-artist-by-id resolver
   :Song/artist (constantly nil)})

(defmethod ig/init-key ::schema
  [_ _]
  (-> (io/resource "aqoursql/graphql-schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers resolver-map)
      schema/compile))

(defmethod ig/init-key ::service
  [_ {:keys [schema options]}]
  (lacinia/service-map schema options))
