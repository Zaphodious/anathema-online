(ns anathema-online.ui.fields
  (:require [rum.core :as rum]
            [anathema-online.ui.common :as ui.common]))

(defmulti form-field-for :field-type)

(rum/defc text-field < rum/static
          [{:keys [path value options read-only class  special-change-fn change-fn view-atom]}]
          (if read-only
            [:span.input-readonly {:class class} value]
            [:input.field {:type      :text, :value value, :id (pr-str path)
                           :key       (pr-str path)
                           :class     (str class " " (if read-only "read-only" ""))
                           :on-change (if special-change-fn special-change-fn
                                                            (ui.common/make-change-handler path change-fn read-only))
                           :readOnly  read-only}]))