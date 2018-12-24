(ns aqoursql.resolver.artists-test
  (:require [aqoursql.resolver.artists :as sut]
            [aqoursql.test-helper.core :as helper :refer [with-db-data with-system]]
            [aqoursql.test-helper.db-data :as db-data]
            [clojure.test :as t]
            [venia.core :as venia]))

(t/use-fixtures
  :once
  helper/instrument-specs)

(t/deftest test-fetch-artist-by-id
  (with-system [sys (helper/test-system)]
    (with-db-data [sys {:artist db-data/artist
                        :artist_member db-data/artist_member
                        :member db-data/member
                        :organization db-data/organization}]
      (t/testing "membersを指定した場合"
        (t/testing "取得できる"
          (let [{:keys [status body]}
                (helper/http-post sys "/graphql"
                                  (venia/graphql-query
                                   {:venia/queries [[:artist_by_id {:id 1}
                                                     [:id
                                                      :type
                                                      :name
                                                      [:members
                                                       [:id
                                                        :name
                                                        :organization_id
                                                        :organization_name]]]]]}))]
            (t/is (= 200 status))
            (t/is (= {:data {:artist_by_id {:id 1
                                            :type 1
                                            :name "Aqours"
                                            :members [{:id 1
                                                       :name "黒澤 ダイヤ"
                                                       :organization_id 1
                                                       :organization_name "浦の星女学院"}
                                                      {:id 2
                                                       :name "渡辺 曜"
                                                       :organization_id 1
                                                       :organization_name "浦の星女学院"}
                                                      {:id 3
                                                       :name "津島 善子"
                                                       :organization_id 1
                                                       :organization_name "浦の星女学院"}]}}}
                     (-> body helper/<-json)))))
        (t/testing "存在しないIDを指定すると取得できない"
          (let [{:keys [status body]}
                (helper/http-post sys "/graphql"
                                  (venia/graphql-query
                                   {:venia/queries [[:artist_by_id {:id 100}
                                                     [:id
                                                      :type
                                                      :name
                                                      [:members
                                                       [:id
                                                        :name
                                                        :organization_id
                                                        :organization_name]]]]]}))]
            (t/is (= 200 status))
            (t/is (= {:data {:artist_by_id nil}}
                     (-> body helper/<-json))))))
      (t/testing "memberを指定しない場合"
        (t/testing "取得できる"
          (let [{:keys [status body]}
                (helper/http-post sys "/graphql"
                                  (venia/graphql-query
                                   {:venia/queries [[:artist_by_id {:id 1}
                                                     [:id
                                                      :type
                                                      :name]]]}))]
            (t/is (= 200 status))
            (t/is (= {:data {:artist_by_id {:id 1
                                            :type 1
                                            :name "Aqours"}}}
                     (-> body helper/<-json)))))
        (t/testing "存在しないIDを指定すると取得できない"
          (let [{:keys [status body]}
                (helper/http-post sys "/graphql"
                                  (venia/graphql-query
                                   {:venia/queries [[:artist_by_id {:id 100}
                                                     [:id
                                                      :type
                                                      :name]]]}))]
            (t/is (= 200 status))
            (t/is (= {:data {:artist_by_id nil}}
                     (-> body helper/<-json)))))))))
