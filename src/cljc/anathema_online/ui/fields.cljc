(ns anathema-online.ui.fields
  (:require [rum.core :as rum]
            [anathema-online.ui.common :as ui.common]
            [com.rpl.specter :as sp]))

(defmulti form-field-for :field-type)

(rum/defc text-field < rum/static
          [{:keys [path value options read-only class special-change-fn change-fn]}]
          (if read-only
            [:span.input-readonly {:class class} value]
            [:input.field {:type      :text, :value value, :id (pr-str path)
                           :key       (pr-str path)
                           :class     (str class " " (if read-only "read-only" ""))
                           :on-change (if special-change-fn special-change-fn
                                                            (ui.common/make-change-handler path change-fn read-only))
                           :readOnly  read-only}]))

(rum/defc text-area < rum/static
  [{:keys [path value options read-only change-fn]}]
  (if read-only
    [:span.input-readonly value]
    [:textarea.field {:id        (pr-str path)
                      :key       (pr-str path)
                      :value     value
                      :class     (if read-only "read-only" "")
                      :readOnly  read-only
                      :on-change (ui.common/make-change-handler path change-fn read-only)}]))

(defn apply-field-map [ui-comp-fn {:keys [value path view-atom pretty-fn]
                                   :or {pretty-fn (fn [a] a)}
                                   :as fieldmap}]
  (if value
    (-> fieldmap
        (dissoc :view-atom)
        (assoc :value (pretty-fn value))
        (ui-comp-fn))
    (-> fieldmap
        (assoc :value (->> view-atom (sp/select-first [sp/ATOM (apply sp/keypath path)]) (pretty-fn)))
        (dissoc :view-atom)
        (ui-comp-fn))))

(defmethod form-field-for :text
  [a] (apply-field-map text-field a))

(defmethod form-field-for :big-text
  [a] (apply-field-map text-area a))