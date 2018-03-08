(ns anathema-online.components.editor
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :as async]
            [com.rpl.specter :as sp]))

(defrecord EditorComponent []
  component/Lifecycle
  (start [{{:keys [read-chan write-chan write-mult] :as disk} :disk
           :as editor}]
    (let [change-event-chan (async/chan 5)
          request-data-chan (async/chan 5)]
      (-> editor
          (assoc :change-event-chan change-event-chan)
          (assoc :request-data-chan request-data-chan))))
  (stop [editor]))

(defn new-editor []
  (->EditorComponent))