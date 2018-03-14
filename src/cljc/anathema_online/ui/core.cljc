(ns anathema-online.ui.core)

(defmulti page-for-category
          (fn [entity ui-component]
            (:category entity)))
