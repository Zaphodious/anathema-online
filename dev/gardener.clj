(ns gardener
  (:require [garden.core :as g]
            [garden.def :as gd]
            [garden.media :as gm]
            [garden.color :as gc]
            [garden.arithmetic :as ga]
            [garden.selectors :as gs]
            [clojure.string :as str]
            [clojure.set :as set]
            [garden.stylesheet :as gss :refer [at-media]]
            [garden.types :as gt]))

(gd/defcssfn url)
(gd/defcssfn blur)
(gd/defcssfn calc)
(gd/defcssfn src)
(gd/defcssfn linear-gradient)

(defn supports [support-statement garden-seq]
  (fn [previous-css] (str previous-css "\n\n\n" "@Supports (" support-statement ") {\n     " (g/css garden-seq) "}")))

(defn grid-area-strings [& stringers]
  (reduce str
          (map
            (fn [a] (str "\n\"" a "\""))
            stringers)))

(defn name-or-string [thing]
  (try
    (name thing)
    (catch Exception e
      (str thing))))

(defn name-if-not-symbol [thing]
  (if (symbol? thing) thing (name-or-string thing)))

(def opmap {- " - " + " + " * " * " / " / "})

(defn replace-operators [possible-op]
  (if-let [oper (get opmap possible-op)]
    oper
    possible-op))

(defn calchelper [& args]
  (let [calcstring (->> args (map replace-operators) (map name-or-string) (map #(str % " ")) (reduce str) str/trim)]
    (calc calcstring)))

(defn unit-fn-for [unittype]
  (fn [n] (str (double n) unittype)))

(def -px (unit-fn-for "px"))
(def -em (unit-fn-for "em"))
(def -% (unit-fn-for "%"))

(defn quoth [n]
  (str "/'" n "/'"))

(def colors
  {:titlebar-back (gc/rgba 0, 0, 0, 0.64)
   :titlebar-text (gc/rgba 1, 1, 1, 1)})

(def color-p-main (gc/hex->rgb "#795548"))
(def color-p-light (gc/hex->rgb "#a98274"))
(def color-p-lighter (gc/lighten color-p-light 10))
(def color-p-dark (gc/hex->rgb "#4b2c20"))
(def color-p-darker (gc/darken color-p-dark 0.5))
(def color-brightest (gc/from-name :white))
(def color-text-bright (gc/hex->rgb "#e0e0e0"))
(def color-darkest (gc/from-name :black))
(def color-off-dark (gc/lighten color-darkest 0.2))

(def brown {:main               "#795578"
            :accent             "#FFC7B3"
            :element-active     "#A67563"
            :element-lighter    "#8C6354"
            :element-darker     "#66483D"
            :background-lighter "#4D362E"
            :background-darker  "#33241F"})

(def sun-gold (gc/hex->rgb "#f4c53e"))
(def moon-blue (gc/complement sun-gold))

(def gbb-opac 0.8)
(def section-title-gradient [(url "../img/brushed_metal.png")
                             (linear-gradient
                               (assoc (gc/darken sun-gold 45) :alpha  gbb-opac)
                               (assoc (gc/darken sun-gold 40) :alpha  gbb-opac)
                               (assoc (gc/darken sun-gold 35) :alpha  gbb-opac)
                               (assoc (gc/darken sun-gold 30) :alpha  gbb-opac)
                               (assoc (gc/darken sun-gold 25) :alpha  gbb-opac)
                               (assoc (gc/darken sun-gold 05) :alpha  gbb-opac)
                               (assoc (gc/lighten sun-gold 20) :alpha  gbb-opac))])

(def title-background-image [;(url "../img/canvas_transparent_header.png")
                             (url "../img/brushed_metal.png")
                             (linear-gradient
                               (assoc (gc/darken sun-gold 35) :alpha  gbb-opac)
                               (assoc (gc/darken sun-gold 30) :alpha  gbb-opac)
                               (assoc (gc/darken sun-gold 25) :alpha  gbb-opac)
                               (assoc (gc/darken sun-gold 20) :alpha  gbb-opac)
                               (assoc (gc/darken sun-gold 15) :alpha  gbb-opac)
                               (assoc (gc/lighten sun-gold 5) :alpha  gbb-opac)
                               (assoc (gc/lighten sun-gold 30) :alpha  gbb-opac))])


(def menu-background-image
  (into [(linear-gradient
           (assoc (gc/lighten sun-gold 25) :alpha 0.5)
           (assoc (gc/darken sun-gold 20) :alpha 0.5))]
        title-background-image))

(def input-background [(url "../img/brushed_metal.png")
                       (linear-gradient
                         (assoc (gc/lighten sun-gold 20) :alpha 0.4)
                         (assoc (gc/lighten sun-gold 20) :alpha 0.5)
                         (assoc (gc/lighten sun-gold 30) :alpha 0.5)
                         (assoc (gc/lighten sun-gold 40) :alpha 0.4))])

(def button-bar-background [(url "../img/brushed_metal.png")
                            (linear-gradient
                              (assoc (gc/darken sun-gold 20) :alpha 0.3)
                              (assoc (gc/darken sun-gold 20) :alpha 0.5)
                              (assoc (gc/darken sun-gold 15) :alpha 0.7)
                              (assoc (gc/darken sun-gold 15) :alpha 0.6)
                              (assoc (gc/darken sun-gold 20) :alpha 0.5)
                              (assoc (gc/darken sun-gold 50) :alpha 0.3))])



(def title-color (gc/lighten sun-gold 10))


(defn prefix-it [stylekey stylerule]
  (let [prefixes ["moz" "webkit" "o"]
        stylekeys (map (fn [a] (keyword (str "-" a "-" (name stylekey))))
                       prefixes)]
    (into {} (map #(vector % stylerule) stylekeys))))

(def title-bar-height "3em")
(def navshadow "0 0 15px black")
(def elementshadow (str "0 -3px 10px " (gc/as-hex (gc/desaturate (gc/darken (gc/mix (gc/complement sun-gold) sun-gold) 20) 20)))) ;#6d6d6d
(def minor-button-shadow (str "0 0 4px " (gc/as-hex (gc/desaturate (gc/darken (gc/mix (gc/complement sun-gold) sun-gold) 20) 20)))) ;#6d6d6d
(def inputshadow (str "inset " minor-button-shadow))
(def buttonshadow (str "0 -2px 10px" (gc/as-hex color-p-dark)))
(def focusshadow (str "0 -3px 5px" (gc/as-hex color-p-dark)))
(def focusshadowtext (str "0 -5px 10px" (gc/as-hex color-p-dark)))
(def section-inner-shadow (str "inset " navshadow))
(def menu-tab-shadow "0 0 5px black")
(def menu-shadow (str "inset " menu-tab-shadow))
(def title-text-shadow (str "0 0 10px " (gc/as-hex (gc/darken sun-gold 10))))



(def page-content-margin-scalar 7)
(def page-content-margin (keyword (str page-content-margin-scalar "px")))
(def standard-field-width (calchelper :100% - :80px - :10px - :2em - page-content-margin - page-content-margin))

(gs/defselector td "td")
(gs/defselector tr "tr")

(def mobilestyle
  [[:&:focus {:outline-style  :solid
              :outline-width  :1px
              :outline-color  (assoc (gc/as-rgb color-p-lighter) :alpha 0.5)
              :outline-offset :0px
              :outline-radius :5px
              :box-shadow     focusshadow
              :z-index        100}]

   [:* {:margin      0
        :font-family "Karma, sans-serif"
        :font-weight :normal
        :font-size   :13px
        :color       color-off-dark}]
   [:h1 :h2 :h3 :h4 :h5 :h6
    {:font-size   :25px
     :font-family "Envision, serif"
     :font-weight :bold
     :color       (gc/darken (gc/complement sun-gold) 50)}
    [:p {:font-weight :normal}]]

   [:button {:background-color (assoc (gc/lighten sun-gold 40)
                                 :alpha 0.4)
             :border-style     :solid
             :border-width     :1px
             :border-color     :none
             :box-shadow       buttonshadow
             :text-shadow      (str "0 0 6px" (gc/as-hex color-p-light))
             :border-radius    (-px 4)
             :color            color-darkest
             :margin-right     :2px
             :margin-left      :2px}]

   ;:border-radius (-px 5)}]
   [:input :select :textarea {:background-color :transparent ;(gc/rgba 255 255 255 0.0)
                              :background-image input-background
                              ;:border-radius :9px
                              :box-shadow       inputshadow
                              ;:border-bottom    :solid
                              ;:border-style :double
                              :border           :none
                              :border-width     :1px
                              :border-color     :grey
                              :border-left      :none
                              :border-right     :none
                              :border-top       :none
                              :height           (-% 75)
                              :margin-bottom    (-px 3)
                              :margin-left      (-px 5)
                              :padding          :5px
                              :padding-right    0
                              :vertical-align   :bottom}
    ;:margin-left (-px 3)}
    [:&:focus {:outline          :none
               :box-shadow       elementshadow
               :border-width     :3px
               :border-bottom    :double
               :margin-bottom    (-px 1)
               :background-color color-text-bright}]]

   ;:box-shadow   focusshadowtext}]]
   ;:border-width :3px}]]
   ;:border-radius (-px 5)}]
   ["input[type=checkbox]" {:height :20px
                            :width  :20px}
    [:&:focus {:height :30px}]]
   [:html {:height :100%}]
   [:body {;:background-color (:background-darker brown)
           :background-image      (url "../img/solar_bg.jpg")
           :background-repeat     :no-repeat
           :background-attachment :fixed
           :background-position   [:right :center]
           :background-size       :cover}]
   ;:height                :100%}]
   ;:overflow              :hide}]
   [:#app {:width :100% :height :100%}] ;:overflow :hide}]
   [:#app-frame {;:position   :fixed
                 ;:overflow-y :hidden
                 ;:overflow-x :auto
                 :top        0
                 :left       0
                 :width      :100%
                 :height     :100%}
    [:.modal-blur {:position         :fixed
                   :top              0
                   :bottom           0
                   :left             0
                   :right            0
                   :background-color (gc/rgba 150 90 70 0.4)
                   :z-index          99}]
    [:.modal-window {:position            :fixed
                     ;:height           :25%
                     :top                 :5%
                     :bottom              :5%
                     :left                :10%
                     :right               :10%
                     :z-index             100
                     :background-color    :white
                     :background-image    [(url "../img/brushed_metal.png")
                                           (url "../img/koi_modal_back.jpg")]
                     :background-size     :cover
                     :background-position :right
                     :border-radius       :20px
                     :box-shadow          elementshadow}
     [:h3.modal-title {:background-image title-background-image
                       :padding          :10px
                       :border-radius    "20px 20px 0 0"}]
     [:.button-bar {:float    :right
                    :position :absolute
                    :bottom   0
                    :right    0
                    :padding  :15px}]
     [:.element-search
      [:input :select
       {:width         (calchelper :100% - :50px)
        :margin-left   :15px
        :margin-top    :3px
        :height        :50px
        :font-size     :20px
        :padding-left  :10px
        :padding-right :10px}]
      [:ul {:overflow   :scroll
            ;:border :solid
            :display    :block
            :position   :absolute
            :right      0
            :left       0
            :bottom     :50px
            :top        :110px
            :padding    0
            :box-shadow section-inner-shadow}
       [:li {;:font-size :1.5em
             :position            :relative
             :list-style          :none
             :height              (calchelper :auto + :20px)
             :margin              :10px
             :background-size     :cover
             :background-position :top-right
             :background-color :blue
             :box-shadow          elementshadow}
        [:&.selected {:border        :solid
                      :border-color  :white
                      :border-width  :4px
                      ;:border-radius :20px
                      :height        :auto}
         [:.select-title :.line
          {:left             :0px
           :top              :0px
           :color            (gc/lighten moon-blue 35)
           :background-color (assoc (gc/darken moon-blue 45) :alpha 0.7)}]]
        [:&.disallowed {:border-color :red}]
        [:.select-title :.line
         {:background-color (assoc (gc/darken sun-gold 45) :alpha 0.7)
          :color            (gc/lighten sun-gold 35)
          :text-shadow "0px 0px 7px black"
          :display          :block
          :position         :relative
          :top              :0px
          :left             :0px
          :width            (calchelper :100% - :3px - :18px)
          :padding          :3px
          :padding-left :18px
          :overflow         :wrap}
         [:&:after {:display :block}]]
        [:&.disallowed [:.select-title :.line {:color (gc/lighten moon-blue 30)
                                               :background-color (assoc (gc/darken sun-gold 60) :alpha 0.8)}]]
        ;:text-shadow "0px 0px 2px white"}]]
        ;[:.select-byline [:&:before {:content "\"By: \""}]]
        [:.select-contains
         {:max-width (calchelper :100% - :25px)}]]]]
     ;[:&:before {:content "\"Contains: \""}]]]]]
     [:.merit-modal-interior
      [:ul {:bottom :170px}]
      [:form {:position :absolute
              :bottom :45px
              :width :100%}]]
     [:.charm-modal-interior
      [:ul {:top :180px}]]]

    [:.menu-assembly {:position :absolute
                      :width    :100%}
     [:button.menu-toggle {:position :fixed
                           :right    :20px
                           :top      :3px
                           :z-index  120}]
     [:.page-menu {:background-image menu-background-image
                   :position         :fixed
                   :box-shadow       "inset 0 0 5px grey"

                   :z-index          109
                   ;:box-shadow elementshadow
                   :transition       "top .5s, opacity .5s"
                   :width            (calchelper :100% - :40px)
                   :padding          :5px}
      [:ul
       [:li {:display       :inline-block
             :padding-right :5px}]]]

     [:.menu-showing {:top     :35px
                      :left    :13px
                      :opacity 1}]
     [:.menu-hidden {;:position :fixed
                     :opacity 0
                     :top     :-70px
                     :left    :13px}]]]
   [:.minimized-field {:transition "opacity .5s"}]
   [:#app-frame.minimized [:* [:.minimized-field {:opacity 0
                                                  :display :none}]]]
   [:#app-frame.page.maximized [:* [:.minimized-field {:opacity 1
                                                       :display :inherit}]]]


   [:.page {:width    :100%
            :height   (calchelper :100% + :30px)
            :position :relative}
    ;:top :30px}
    [:h1.page-title {:width            :100%
                     :position :fixed
                     :top 0
                     ;:background-color (:element-darker brown)

                     ;:color            color-text-bright
                     ;:background  page-title-gradient
                     :background-image title-background-image

                     ;(gc/darken sun-gold 50)
                     ; :color            title-color
                     ;:text-shadow      title-text-shadow
                     :text-align       :center
                     :font-weight      :bolder
                     :box-shadow       elementshadow
                     :text-shadow      title-text-shadow
                     :z-index          110}]


    ;:right :0px}]]
    [:.page-content {};:height     (calchelper :100vh - title-bar-height - :10px)
     ;:overflow-y :auto
     ;:overflow-x :hidden}

     [:.element-button-bar {:background-color :transparent
                            :margin           (-px (* page-content-margin-scalar 2))
                            :margin-bottom    (-px (- (* -1 page-content-margin-scalar 6.6) 1))
                            ;:box-shadow       elementshadow
                            :text-align       :right
                            :z-index          150
                            :position         :relative
                            :top              :30px
                            :right            :5px
                            :padding          (-px 7)}]
     [:a {:height :auto}]

     [:.page-section {;:background-color (:element-darker brown)
                      ;:background-color (gc/rgba 255 255 255 0.4)
                      :background-image (url "../img/canvas_paper.png")
                      :margin           (-px (* page-content-margin-scalar 1))
                      ;:box-shadow       elementshadow
                      :padding          :5px
                      :padding-top      0
                      :margin-bottom    :10px
                      ;:border :solid
                      :border-width     :1px
                      :border-color     sun-gold
                      ;:border-color section-border-color
                      ;:border-style :solid
                      ;:border-width :3px
                      ;:border-bottom-left-radius :30px
                      ;:border-top-right-radius :30px
                      :position         :relative
                      :top :30px
                      :box-shadow       elementshadow}
      [:&.button-bar {:padding :10px}]
      [:& {:padding-bottom 0}]
      [:&.soft-table
       [:.table-container {:overflow-y :scroll
                           :border :solid
                           :border-width :1px
                           :border-color (gc/darken moon-blue 30)}
        [:table {:table-layout :fixed
                 :width :100%
                 :border-collapse :collapse}
         [:tbody]

         [:tr {:height :1em
               :background-color (assoc (gc/darken (gc/as-rgb moon-blue) 25)
                                   :alpha 0.2)}
          [:td :th {:padding :5px}]
          [:th {:background-image title-background-image
                :border-radius :0px
                :padding :0px}
           [:&.button {:width :30px}]
           [:&.name {:width :150px}]
           [:&.dots {:width :130px
                     :max-width :130px}]
           [:&.word {:width :90px}]
           [:&.number {:width :40px}]
           [:&.description {:width :500px}]]
          [:td {:overflow         :hidden
                :text-overflow    :ellipsis
                :background-color (assoc (gc/lighten (gc/as-rgb moon-blue) 35)
                                    :alpha 0.4)}
           [:&.description {:height :1em}]
           [:input :select {:width :100%
                            :margin-left 0}]
           [:.field.number-field
            [:input {:width :2em}]]]
          [(td (gs/nth-child 2)) {:background-color (assoc (gc/lighten (gc/as-rgb sun-gold) 35)
                                                      :alpha 0.4)}]]
         [(tr (gs/nth-child 2)) {:background-color (assoc (gc/darken (gc/as-rgb sun-gold) 25)
                                                     :alpha 0.2)}]]]]
      [:&.rulebooks-used
       [:input {:width :100%}]]
      [:&.page-header {:background-position :top
                       :background-size     :cover
                       :min-height          :200px}
       [:p {:background-color (assoc (gc/as-rgb (gc/darken sun-gold 50)) :alpha 0.7)
            :color            (gc/lighten sun-gold 20)
            :display          :block
            :position         :relative
            :top              :10px
            :margin-bottom    :20px
            :padding          :2px
            :font-size        :1.25em}]]
      [:img {:max-width  :50%
             :max-height :500px
             :overflow   :hidden}]
      ;:border-top-right-radius :30px
      ;:border-bottom-left-radius :30px}]
      [:img.banner-image {:display    :block
                          :text-align :center
                          :margin     (-px -10)
                          :margin-top (-px 0)
                          :max-width  (calchelper (-% 100) + (-px 20))}]

      [:img.profile-image {:max-width    :50%
                           :display      :inline
                           :margin       (-px 1)
                           :margin-top   (-px 5)
                           :border       :inset
                           :border-width (-px 1)}]
      [:.profile-text {:float      :right
                       :display    :inline
                       :width      (calchelper :50% - (-px 20))
                       :text-align :left
                       :margin     (-px 5)}]
      [:h1 :h2 {:text-align :justify}]
      [:h3 :h4 :h5 :h6 {:text-align       :justify
                        :border-bottom    :solid
                        :border-color     "#6f98a9"
                        :position :sticky
                        :top :30px
                        :z-index 100

                        :background-image title-background-image
                        ;(assoc (gc/as-rgb (gc/from-name "white")) :alpha 0.7))]
                        :border-width     :1px
                        :padding          :4px
                        :padding-left     :15px
                        :padding-top      :7px
                        :margin-top       :0px
                        :margin-left      :-5px
                        :margin-right     :-5px
                        ;:background    section-title-gradient
                        ;:color            (gc/lighten sun-gold 10)
                        :text-shadow      title-text-shadow}]
      ;:box-shadow    elementshadow}]
      [:.button-bar {:padding :10px}
       [:button {:margin :4px}]]
      [:.navlist-container
       [:.navlist-selected {:tab-index 1}]
       [:ul.field.navlist.hidden {:display :none}]
       [:ul.field.navlist.shown {:display :block}]]]]]

   ;:box-shadow       :none}]]]


   ;:height (calchelper :100% - :40px)}
   [:.dot-bar {:display :inline-block
               :height :15px
               :padding :.3em
               :padding-left 0
               :padding-right 0
               :padding-top :5px
               :position :relative
               :top :5px
               :background-image button-bar-background
               :box-shadow inputshadow
               :border-radius :10px}
    [:.dot-entry {:width (-em 2.5)}]
    [:button {:height :13px
              :width :13px
              :padding 0
              :border :none
              :color :transparent
              :border-radius :20px
              :position :relative
              :bottom :1px
              :box-shadow minor-button-shadow
              :background-color :transparent
              :background-position :center
              :background-size :cover}]
    [:.minus {:background-image (url "../img/screw_minus.png")}]
    [:.plus {:background-image (url "../img/screw_plus.png")}]
    [:.inactive-dot :.active-dot {:margin 0,
                                  :margin-right :1px
                                  :margin-left :1px
                                  :padding (-px 0)
                                  :display :inline-block
                                  :width :15px
                                  :height :15px
                                  :border-radius :20px
                                  ;:box-shadow minor-button-shadow
                                  :background-position :center
                                  :background-size :cover
                                  :position :relative
                                  :botom :3px}]]
   [:form
    [:* {:padding :.5em}]
    [:&.mini-form {:padding :0px
                   :margin  :5px
                   :display :block}]
    [:p {:padding :3px}
     [:label {:width      (calchelper :20% - :10px)
              :height     (-% 100)
              :display    :inline-block
              :text-align :right
              :padding-right :10px}]
     [:.field {:width   (calchelper :100% - :20% + :10px - :10px - :2em - page-content-margin - page-content-margin)
               :display :inline-block
               ;:margin  (-px (/ 2 page-content-margin-scalar))
               :align   :center}
      ;[:&:before {:content "\"⚫\""}]]
      [:.zero-dot {:background-image (url "../img/dot_bar.png")}]
      [:.favored {:display             :inline-block
                  :width               :10px
                  :height              :10px
                  :background-position :center
                  :background-size     :cover
                  :position            :relative
                  :top                 :8px
                  :right :5px}]
      [:.selected.dawn {:background-image (url "../img/dawn.png")}]
      [:.selected.twilight {:background-image (url "../img/twilight.png")}]
      [:.selected.night {:background-image (url "../img/night.png")}]
      [:.selected.eclipse {:background-image (url "../img/eclipse.png")}]
      [:.selected.zenith {:background-image (url "../img/zenith.png")}]
      [:.not-selected {:background-image :none}]]
     ;(url "../img/caste_check.png")}]]
     [:.number-field
      [:input {:width (calchelper :100% - :80px)}
       [:&.fraction-top {:width (calchelper :50% - :13px)}]]
      [:span.fraction-bottom {:display :inline-block
                              :width (calchelper :50% - :80px)}
       [:&:before {:content "\" Out of \""}]]
      [:button {:width :20px}]]
     [:.read-only {:border 0,}]
     ;:background-color color-brightest}]
     [:textarea {:height :3.2em}]]
    [:.mini-label {:display :none}]

    [:.dec-button {:display :inline-block}]
    [:.first-of-three :.second-of-three :.third-of-three {:display :inline-block}]
    [:.first-of-three :.second-of-three {:width (calchelper (-% 26) - :50px)}]
    [:.third-of-three {:width (calchelper (-% 60) - :37px)}]]
   [:form.mini-form {:width   (calchelper (-% 100) - :25px)
                     :display :inline-block}]
   [:.inactive-dot {:background-image (url "../img/dot_empty.png")}]
   ;[:&:before {:content "\"⚪\""}]]
   [:.active-dot {:background-image (url "../img/eclipse_dot.png")}]
   [:.eclipse [:.active-dot {:background-image (url "../img/eclipse_dot.png")}]]
   [:.twilight [:.active-dot {:background-image (url "../img/twilight_dot.png")}]]
   [:.night [:.active-dot {:background-image (url "../img/night_dot.png")}]]
   [:.dawn [:.active-dot {:background-image (url "../img/dawn_dot.png")}]]
   [:.zenith [:.active-dot {:background-image (url "../img/zenith_dot.png")}]]
   #_[:.max-willpower [:.active-dot {:background-image (url "../img/square_gem_eclipse.png")
                                     :border-radius :0px
                                     :background-repeat :no-repeat}]]
   [:.eclipse [:* [:.max-willpower [:.active-dot {:background-image (url "../img/square_gem_eclipse.png")}]]]]
   [:.twilight [:* [:.max-willpower [:.active-dot {:background-image (url "../img/square_gem_twilight.png")}]]]]
   [:.dawn [:* [:.max-willpower [:.active-dot {:background-image (url "../img/square_gem_dawn.png")}]]]]
   [:.night [:* [:.max-willpower [:.active-dot {:background-image (url "../img/square_gem_night.png")}]]]]
   [:.zenith [:* [:.max-willpower [:.active-dot {:background-image (url "../img/square_gem_zenith.png")}]]]]
   [:.set-selectors
    [:.set-selector {:width (-% 100)}]]
   [:span.rank-selection {:width   :1.5em
                          :height  :1.5em
                          :padding 0
                          :margin  0
                          ;:margin-top :1em
                          :display :inline-block}
    [:&:focus-within
     [:span.select-helper {:display          :inline-block
                           ;:float            :right
                           :opacity          :0.5
                           :background-color (gc/darken color-text-bright 30)
                           :height           :20px
                           :width            :10px
                           :padding          :0px
                           :margin           0
                           :position         :relative
                           :bottom           :24px
                           :right            :8px
                           :border-radius    :10px
                           :z-index          2}]]
    [:span.select-helper {:display :none}]

    [:input {:display     :inline
             :margin      0
             :opacity     0
             :width       :15px
             :height      :15px
             :position    :relative
             :z-index     10
             :right       :0px
             :bottom      :7px
             ;:margin-bottom :-30px}]
             ;:width :1px}]
             :margin-left :-18px}]

    [:label {:width          :10px
             :height         :10px
             :padding        :5px
             :padding-bottom :12px
             :text-align     :center
             :display        :inline-block
             :border-radius  :10px
             :z-index        6
             :position       :relative}]

    [:.checked {:border       :solid
                :border-width :1px}]]
   [:.balanced-number-field {:width        :110%            ;:max-width :200px}
                             ;:border :solid
                             :margin-right :-10px}
    [:input {:width :15px}]
    [:label {:font-size :0.5em
             :padding   :0}]
    [:button {:width   :15px
              :padding 0}]]
   [:ul.health-track {:padding    0
                      :text-align :justify}
    [:li.health-box {:display             :inline-block
                     :padding             0
                     :margin              :2px
                     :box-shadow          elementshadow
                     :min-width           :28px
                     :min-height          :28px
                     :max-width           :100px
                     :max-height          :100px
                     :width               :36px
                     :height              :36px
                     :border              :outset
                     :border-width        :1px
                     :border-radius       :10px
                     :background-color    :white
                     :vertical-align      :center
                     ;:background-image (url "../img/damage_blank.png")
                     :background-size     :70%
                     :background-repeat   :no-repeat
                     :background-position "right bottom"}]
    [:li.bashing-damage {:border-color     :blue
                         :background-image (url "../img/quick-slash.png")}]
    [:li.lethal-damage {:border-color     :red
                        :background-image (url "../img/perpendicular-rings.png")}]

    [:li.aggravated-damage {:border-color     :green
                            :background-image (url "../img/spikes-half.png")}]]
   [:button.bashing {:border-color :blue}]
   [:button.lethal {:border-color :red}]
   [:button.aggravated {:border-color :green}]
   [:.paste-entry-field {:width (calchelper :100% - :10px)}]
   [:.soft-table-row {;:background-color :blue
                      :position     :relative
                      :top          0
                      :padding-left :30px}
    [:button {:position :absolute
              :top      :5px
              :left     :5px}]]
   [:.meritinfo
    [:.soft-table-row
     [:label {:margin 0, :padding 0}]
     [:.rank {:width :2em}]
     [:.note {:padding-left 0
              :margin-left  0}
      [:&:before {:content "\"| \""}]]
     [:.name {:width :45%}]]]

   ;:word-wrap :break-word
   ;:display :inline-block}]]]   ;{:background-color :blue}]]
   [:.intimacyinfo
    [:.soft-table-row]
    ;[:button {:top :-20px}]]

    [:.soft-table-row {}]                                   ;:border :solid}]
    [:select {:width :100%}]
    [:input {
             :width (calchelper :100% - :20px)}]]])

(def mobilestyle-new
  [[:* {:margin 0
        :padding 0
        :font-family "opendyslexic, sans-serif"
        :font-weight :normal
        :font-size :13px}]
   [:html {:height  (calchelper :100% - :20px)
           :background-image (url "../img/solar_bg.jpg")
           :background-repeat :no-repeat
           :background-attachment :fixed
           :background-position [:right :bottom]
           :background-size :cover}]
   [:body {:height :100%
           :width :100%
           :position :relative}]
   [:h1 :h2 :h3 :h4 :h5 :h6
    {:font-size   :25px
     :font-family "Envision, serif"
     :font-weight :bold
     :color       (gc/darken moon-blue 45)}
    [:p {:font-weight :normal}]]
   [:#app {:width :100%}]
   [:#app-frame {:top          0
                 :left         0
                 :height       :100%
                 :margin-right 0
                 ;:margin-bottom :-40px
                 :width        :100% ;(calchelper :100% - :10px)
                 :position     :relative}
    [:.page-title {:position         :fixed
                   :top              0
                   :padding-top      :5px
                   :width            :100%
                   :height           :40px
                   :font-size        :37px
                   :background-image title-background-image
                   :text-shadow      title-text-shadow
                   :text-align       :center
                   :z-index          110}
     [:h1 {:font-size :inherit}]]
    [:#menu {
             :color :white
             ;:text-shadow navshadow
             :display :block
             :position :fixed
             :height :200px
             :width :100%
             ;:top :45px
             :bottom :-200px
             :left :0px
             :transition [:left :0.5s]
             :z-index 50}
     [:&:hover {:left :0%
                :bottom :0px}]
     [:ul {:position :relative
           :box-shadow menu-shadow
           :overflow :hide
           :height :100%
           :padding-left :15px
           :background (-> (gc/as-hsl moon-blue)
                           (assoc :saturation 20)
                           (assoc :lightness 50))}
      [:li {:display       :block
            :border-bottom-style :groove
            :border-bottom-color (-> (gc/as-hsl moon-blue)
                                     (assoc :saturation 20)
                                     (assoc :lightness 30))
            :border-bottom-width :1px
            :font-size     :20px
            :width (calchelper :100% - :30px)
            :padding       :4px}
       [:&:last {:border :none}]
       [:&:before {:content "\"\""}]
       [:.menu-icon {:font-size :27px
                     :text-align :center
                     :vertical-align :middle
                     :margin-top :-3px
                     :margin-right :5px}]]]
     [:.tab {:position         :fixed
             :box-shadow       menu-tab-shadow
             :color :white
             :font-size :36px
             ;:padding-left :30px
             :bottom           :-5px
             :right            :10px
             :display          :block
             :border-radius    :5px
             :background-color (-> (gc/as-hsl moon-blue)
                                   (assoc :saturation 20)
                                   (assoc :lightness 50))
             :width            :35px
             :height           :45px}]]
    [:#content {:position :relative}
                ;:top      :0px
                ;:margin-top :45px
                ;:left     :40px
                ;:width    :100%}
     [:.page {:position :relative
              :margin-top :45px}
      [:.section {:background-image (url "../img/canvas_paper.png")
                  :border-width :1px
                  :border-color sun-gold
                  :position :relative
                  :width :100% ;(calchelper :100% - :20px)
                  :box-shadow elementshadow
                  :z-index 10
                  :padding-top 0
                  :margin-bottom :10px}
       [:h3 {:background-image section-title-gradient
             :font-size :20px
             :position :sticky
             :top :45px
             ;:width (calchelper :100% - :20px)
             ;:height (calchelper :100% + :10px)
             :padding :5px}]
       [:.interior {:padding :10px
                    :padding-top :3px}
        [:ul
         [:li {:list-style :none} ;:border :solid}
          [:* {:display :block
               :width :100%}]]]]]]]]
   [:input :select :textarea {:background-color :transparent ;(gc/rgba 255 255 255 0.0)
                              :background-image input-background
                              ;:border-radius :9px
                              :box-shadow       inputshadow
                              ;:border-bottom    :solid
                              ;:border-style :double
                              :border           :none
                              :border-width     :1px
                              :border-color     :grey
                              :border-left      :none
                              :border-right     :none
                              :border-top       :none
                              :height           (-% 75)
                              :margin-bottom    (-px 3)
                              :margin-left      (-px -5)
                              :padding          :5px
                              :padding-right    0
                              :vertical-align   :bottom}
    ;:margin-left (-px 3)}
    [:&:focus {:outline          :none
               :box-shadow       elementshadow
               :border-width     :3px
               :border-bottom    :double
               :margin-bottom    (-px 1)
               :background-color color-text-bright}]]])




(gs/defselector page ".page")
(def desktop-style
  [[:.desktop
    [:.menu-assembly {:position :fixed
                      :left     0
                      :width    :200px}]
    [:.pages {:width    :100%
              :position :fixed
              :right    0}
     [:.page {:display  :inline-block
              :position :fixed
              :top      0}]
     [(page (gs/nth-child 1)) {:width :30%
                               :left  0}]
     [(page (gs/nth-child 2)) {:width :70%
                               :left  :30%}]


     [:.page-content {;:display    :grid
                      ;:overflow-y :scroll
                      :width      :100%}]]]])


#_[:tr
   [:td
    {:position :relative
     :left     :40px}
    [:&:last-child {:background-color :blue}]]]
;[:h1.page-title {:width :900px}]]]]]]])
;[(gt/->CSSAtRule "Supports" {:grid-template-areas "\"...\""})]]);"@Supports (grid-template-areas: \"...\")"


(def character-page-desktop-style
  (supports "grid-template-areas: \"...\""
            [:.desktop
             [:span.row-container {:display :inline-block
                                   :width :100%}]
             [:.page  [:h1.page-title {:position :absolute}]
              [:.page-content {:overflow :scroll
                               :height (calchelper :100% - :30px)}]]
             [:.character-page
              [:.page-content {:display               :grid
                               :grid-template-columns "1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr"}
               [:.page-header {:grid-area "head"}]
               [:.coreinfo {:grid-area "core"}
                [:form {:column-count 2}]]
               [:.rulebooks-used {:grid-area "chrn"}]
               [:.attributeinfo {:grid-area "attr"}
                [:form {:column-count 3}
                 [:.field {:width :50%}]]]

               [:.attributeinfo :.abilityinfo
                [:form
                 [:p {};:border :solid
                  ;:padding-left  :20px
                  ;:padding-right 0}
                  [:label {:width :30%}]

                  [:p {}]]]];:float :right
               ;:margin-right :-30px
               ;:padding      :7px}]]]]
               [:.abilityinfo {:grid-area "abil"}
                [:.field {:width (calchelper :50% + :22px)}]]
               [:.additional-ability-info {:grid-area "abad"}]
               [:.crafting-slot-info {:grid-area "slot"}]
               [:.favoredabilities {:grid-area "favo"}]
               [:.specialtyinfo {:grid-area "spec"}]
               [:.health-track-module {:grid-area "heal"}
                [:.button-bar {:display :inline-block}]]
               [:.intimacyinfo {:grid-area "inti"}]
               [:.charminfo {:grid-area "char"}]
               [:.meritinfo {:grid-area "meri"}]
               [:.weapon-info {:grid-area "weap"}]
               [:.essence-module {:grid-area "essi"}
                [:form {:column-count 1}]]
               [:.willpower-module {:grid-area "will"}]
               [:.experience-module {:grid-area "xpxp"}]
               [:.crafting-experience-module {:grid-area "crxp"}]
               [:.limit-info {:grid-area "limt"}]]
              (gss/at-media {:min-width :700px}
                            [:.page-content {:grid-template-areas (grid-area-strings
                                                                    "head head head head head head head head head"
                                                                    ".... .... .... .... .... .... .... .... ...."
                                                                    "core core core core core core core core core"
                                                                    "core core core core core core core core core"
                                                                    "core core core core core core core core core"
                                                                    "chrn chrn chrn chrn chrn chrn chrn chrn chrn"
                                                                    ".... .... .... .... .... .... .... .... ...."
                                                                    "attr attr attr attr attr attr attr attr attr"
                                                                    "abil abil abil abil abil spec spec spec spec"
                                                                    "abil abil abil abil abil spec spec spec spec"
                                                                    "abil abil abil abil abil abad abad abad abad"
                                                                    "abil abil abil abil abil .... .... .... ...."
                                                                    "abil abil abil abil abil .... .... .... ...."
                                                                    "abil abil abil abil abil .... .... .... ...."
                                                                    "slot slot slot slot slot crxp crxp crxp crxp"
                                                                    "slot slot slot slot slot .... .... .... ...."
                                                                    "meri meri meri meri meri meri meri meri meri"
                                                                    "weap weap weap weap weap weap weap weap weap"
                                                                    "essi essi essi essi essi essi essi essi essi"
                                                                    "xpxp xpxp xpxp xpxp xpxp xpxp xpxp xpxp xpxp"
                                                                    "will will will will will will will will will"
                                                                    "limt limt limt limt limt limt limt limt limt"
                                                                    "heal heal heal heal heal heal heal heal heal"
                                                                    ".... .... .... .... .... .... .... .... ...."
                                                                    "inti inti inti inti inti inti inti inti inti"
                                                                    "inti inti inti inti inti inti inti inti inti"
                                                                    "char char char char char char char char char")}

                             [:.attributeinfo
                              [:form {:column-count 2}]]
                             [:.rulebooks-used
                              [:span.row-container {:display :inline-block
                                                    :width :100%
                                                    :column-count 3}]]
                             [:.coreinfo {:grid-area "core"}
                              [:form {:column-count 1}]]
                             [:.specialtyinfo
                              [:input {:width :100%}]
                              [:select {:width :100%}]]
                             [:.meritinfo
                              [:span.row-container
                               {:display :inline-block
                                :column-count 2}]]])
              #_(gss/at-media {:min-width :900px}
                              [:.page-content {:grid-template-areas (grid-area-strings
                                                                      "head head head head head chrn chrn chrn chrn"
                                                                      ".... .... .... .... .... chrn chrn chrn chrn"
                                                                      "core core core core core core core core core"
                                                                      "attr attr attr attr attr attr attr attr attr"
                                                                      "abil abil abil abil favo favo favo favo favo"
                                                                      "abil abil abil abil favo favo favo favo favo"
                                                                      "abil abil abil abil meri meri meri meri meri"
                                                                      "abil abil abil abil chrn chrn chrn chrn chrn"
                                                                      "abil abil abil abil chrn chrn chrn chrn chrn"
                                                                      "abil abil abil abil .... .... .... .... ...."
                                                                      "xpxp xpxp xpxp xpxp essi essi essi essi essi"
                                                                      "will will will will limt limt limt limt limt"
                                                                      "heal heal heal heal heal heal heal heal heal"
                                                                      ".... .... .... .... .... .... .... .... ...."
                                                                      "spec spec spec spec spec spec spec spec spec"
                                                                      "inti inti inti inti inti inti inti inti inti"
                                                                      "inti inti inti inti inti inti inti inti inti")}
                               [:.attributeinfo
                                [:form {:column-count 2}]]
                               [:.coreinfo {:grid-area "core"}
                                [:form {:column-count 1}]]])
              (gss/at-media {:min-width :1200px}
                            [:.page-content {:grid-template-columns "1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr"
                                             :grid-template-areas   (grid-area-strings
                                                                      "head head head core core core core core core"
                                                                      ".... .... .... .... .... .... .... .... ...."
                                                                      ".... .... .... .... .... .... .... .... ...."
                                                                      "attr attr attr attr attr attr attr attr attr"
                                                                      "abil abil abil spec spec spec meri meri meri"
                                                                      "abil abil abil spec spec spec meri meri meri"
                                                                      "abil abil abil spec spec spec meri meri meri"
                                                                      "abil abil abil spec spec spec meri meri meri"
                                                                      "abil abil abil chrn chrn chrn meri meri meri"
                                                                      "abil abil abil chrn chrn chrn meri meri meri"
                                                                      "abil abil abil abad abad abad abad abad abad"
                                                                      "abil abil abil .... .... .... .... .... ...."
                                                                      "slot slot slot slot slot crxp crxp crxp crxp"
                                                                      "slot slot slot slot slot .... .... .... ...."
                                                                      ".... .... .... .... .... .... .... .... ...."
                                                                      "essi essi essi essi xpxp xpxp xpxp xpxp xpxp"
                                                                      ".... .... .... .... xpxp xpxp xpxp xpxp xpxp"
                                                                      "weap weap weap weap weap weap weap weap weap"
                                                                      "heal heal heal heal heal heal heal heal heal"
                                                                      "inti inti inti inti inti limt limt limt limt"
                                                                      "inti inti inti inti inti will will will will"
                                                                      "inti inti inti inti inti .... .... .... ...."
                                                                      "char char char char char char char char char")}
                             [:.attributeinfo
                              [:form {:column-count 3}]]
                             [:.rulebooks-used
                              [:span.row-container {:column-count 1}]]
                             [:.meritinfo
                              [:span.row-container {:column-count 1}]]
                             [:.intimacyinfo
                              [:span.row-container {:column-count 2}]]
                             [:.specialtyinfo
                              [:span.row-container {:column-count 1}]]
                             [:.coreinfo
                              [:form {:column-count 2}]]])]]))




(defn add-generated-statement [csser]
  (str "/*This file is automatically generated. Any changes made will be overwritten by dev/gardener.clj*/\n\n\n" csser))



(defn compile-style! []
  (spit "resources/public/css/style.css"
        (->> (into mobilestyle-new desktop-style)
             (map g/css)
             (reduce (fn [a b] (str a "\n\n" b)))
             character-page-desktop-style
             add-generated-statement)))

(compile-style!)