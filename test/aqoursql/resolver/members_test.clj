(ns aqoursql.resolver.members-test
  (:require [aqoursql.resolver.members :as sut]
            [aqoursql.test-helper.core :as helper :refer [with-db-data with-system]]
            [aqoursql.test-helper.db-data :as db-data]
            [clojure.test :as t]
            [venia.core :as venia]))

(t/use-fixtures
  :once
  helper/instrument-specs)

(t/deftest test-fetch-member-by-id
  (with-system [sys (helper/test-system)]
    (with-db-data [sys {:member db-data/member
                        :organization db-data/organization}]
      (t/testing "取得できる"
        (let [{:keys [status body]}
              (helper/http-post sys "/graphql"
                                (venia/graphql-query
                                 {:venia/queries [[:member_by_id {:id 1}
                                                   [:id
                                                    :name
                                                    :organization_id
                                                    :organization_name]]]}))]
          (t/is (= 200 status))
          (t/is (= {:data {:member_by_id {:id 1
                                          :name "黒澤 ダイヤ"
                                          :organization_id 1
                                          :organization_name "浦の星女学院"}}}
                   (-> body helper/<-json)))))
      (t/testing "存在しないIDを指定すると取得できない"
        (let [{:keys [status body]}
              (helper/http-post sys "/graphql"
                                (venia/graphql-query
                                 {:venia/queries [[:member_by_id {:id 100}
                                                   [:id
                                                    :name
                                                    :organization_id
                                                    :organization_name]]]}))]
          (t/is (= 200 status))
          (t/is (= {:data {:member_by_id nil}}
                   (-> body helper/<-json))))))))
