(ns anathema-online.ui.common)

(defn get-change-value [e] (.. e -target -value))
(defn make-change-handler [path change-fn readonly?]
  (fn [a] (when (not readonly?)
           (change-fn {:path path :view (get-change-value a)}))))