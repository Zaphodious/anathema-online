(ns anathema-online.application
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.handler :refer [new-handler]]
            [system.components.middleware :refer [new-middleware]]
            [system.components.jetty :refer [new-web-server]]
            [anathema-online.config :refer [config]]
            [anathema-online.routes :refer [home-routes]]
            [anathema-online.components.environ :refer [new-environ]]
            [anathema-online.components.db :refer [new-db]]
            [anathema-online.components.disk :refer [new-disk]]))

(defn app-system [config]
  (component/system-map
    :routes     (new-endpoint home-routes)
    :middleware (new-middleware {:middleware (:middleware config)})
    :handler    (-> (new-handler)
                    (component/using [:routes :middleware]))
    :http       (-> (new-web-server (:http-port config))
                    (component/using [:handler]))
    :disk       (new-disk)
    :environ    (new-environ)
    :db         (-> (new-db)
                    (component/using [:environ :disk]))))

(defn -main [& _]
  (let [config (config)]
    (-> config
        app-system
        component/start)
    (println "Started anathema-online on" (str "http://localhost:" (:http-port config)))))