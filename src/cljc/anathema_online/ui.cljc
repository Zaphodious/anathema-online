(ns anathema-online.ui)

(defmulti page-for-category
          (fn [entity ui-component]
            (:category entity)))