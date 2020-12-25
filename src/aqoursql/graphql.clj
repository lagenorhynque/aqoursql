(ns aqoursql.graphql
  (:require
   [aqoursql.resolver.artists :as artists]
   [aqoursql.resolver.members :as members]
   [aqoursql.resolver.songs :as songs]
   [clojure.java.io :as io]
   [com.walmartlabs.lacinia.parser.schema :as parser.schema]
   [com.walmartlabs.lacinia.pedestal.subscriptions :as subscriptions]
   [com.walmartlabs.lacinia.pedestal2 :as lacinia.pedestal2]
   [com.walmartlabs.lacinia.schema :as schema]
   [integrant.core :as ig]
   [io.pedestal.http :as http]
   [io.pedestal.http.jetty.websockets :as websockets]))

(def attach-map
  {:resolvers {:Query {:artist_by_id artists/fetch-artist-by-id
                       :artists artists/list-artists
                       :member_by_id members/fetch-member-by-id
                       :members members/list-members
                       :song_by_id songs/fetch-song-by-id
                       :songs songs/list-songs}}})

(defmethod ig/init-key ::schema
  [_ {:keys [path]}]
  (-> (io/resource path)
      slurp
      (parser.schema/parse-schema attach-map)))

(defn routes [interceptors {:keys [api-path ide-path asset-path]
                            :as options}]
  (into #{[api-path :post interceptors
           :route-name ::graphql-api]
          [ide-path :get (lacinia.pedestal2/graphiql-ide-handler options)
           :route-name ::graphiql-ide]}
        (lacinia.pedestal2/graphiql-asset-routes asset-path)))

(defmethod ig/init-key ::service
  [_ {:keys [schema options]}]
  (let [compiled-schema (schema/compile schema)
        interceptors (lacinia.pedestal2/default-interceptors compiled-schema (:app-context options))]
    (lacinia.pedestal2/enable-graphiql
     {:env (:env options)
      ::http/routes (routes interceptors options)
      ::http/allowed-origins (constantly true)
      ::http/container-options
      {:context-configurator
       (fn [context]
         (websockets/add-ws-endpoints context
                                      {(:subscriptions-path options) nil}
                                      {:listener-fn
                                       (subscriptions/listener-fn-factory compiled-schema options)}))}})))
