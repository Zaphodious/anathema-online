(ns anathema-online.ui.fields)

(defmulti form-field-for :field-type)

(rum/defc text-field < rum/static
          [{:keys [path value options read-only class  special-change-fn change-fn view-atom]}]
          (if read-only
            [:span.input-readonly {:class class} value]
            [:input.field {:type      :text, :value value, :id (pr-str path)
                           :key       (pr-str path)
                           :class     (str class " " (if read-only "read-only" ""))
                           :on-change (if special-change-fn special-change-fn
                                                            (standard-on-change-for path read-only))
                           :readOnly  read-only}]))