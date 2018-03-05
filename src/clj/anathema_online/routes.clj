(ns anathema-online.routes
  (:require [clojure.java.io :as io]
            [compojure.core :refer [ANY GET PUT POST DELETE routes]]
            [compojure.route :as route :refer [resources]]
            [ring.util.response :refer [response]]))

(defn home-routes [endpoint]
  (routes
    (route/not-found
      (-> "<body>Hello World</body>"
          response
          (assoc :headers ["Content-Type" "text/html; charset=utf-8"])))
    (GET "/" _
      (-> "public/index.html"
          io/resource
          io/input-stream
          response
         (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))
    (resources "/")))
