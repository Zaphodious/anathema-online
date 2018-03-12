(ns anathema-online.routes
  (:require [clojure.java.io :as io]
            [compojure.core :refer [ANY GET PUT POST DELETE routes context]]
            [compojure.route :refer [resources]]
            [anathema-online.disk :as adisk]
            [ring.util.response :as resp :refer [response]]))

(defn home-routes [{:keys [disk] :as endpoint}]
  (routes
    (context "/data" []
      (GET "/" [] (resp/content-type (response "<h1>Thing</h1>") "text/html")))

    (GET "/data/:category/:key.:filetype" [category key filetype]
      (-> (adisk/read-object
            disk
            (keyword category)
            key)
          pr-str
          resp/response
          (resp/content-type "text/edn")))
    ;(assoc :headers {"Content-Type" "text/edn; charset=utf-8"})))

    (GET "/" _
      (-> "public/index.html"
          io/resource
          io/input-stream
          response
          (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))
    (resources "/")))