(ns anathema-online.creation)


(defn- character [name key]
  {:description "One Who's Path is Yet To Be Written",
   :category :character,
   :key key,
   :limit
                {:trigger "When I Drop My Scrolls", :accrued 3},
   :supernal :lore,
   :crafting [{:rating 1, :description "New Heroes"}],
   :rulebooks ["0" "1"],
   :anima "The Ten Stages of the Trintathalon",
   :name name,
   :abilities-additional
                [{:ability :martial-arts,
                  :rank 3,
                  :description "Single Point Shining Into The Void"}],
   :long-description "The One Without A Past",
   :specialties
   [[:athletics "Acrobatics"]
    [:integrity "Resisting Bribery"]
    [:socialize "Read Intentions"]
    [:awareness "Indiscretions"]
    [:lore "Desert Legends"]
    [:lore "Demonology"]],
   :type :solar,
   :crafting-slots [],
   :abilities
   {:lore 5,
    :dodge 2,
    :linguistics 3,
    :socialize 3,
    :bureaucracy 2,
    :brawl 1,
    :athletics 2,
    :integrity 3,
    :awareness 3,
    :medicine 3},
   :willpower {:temporary 2, :max 9},
   :charms
   ["Graceful Crane Stance"
    "Monkey Leap Technique"
    "Sensory Acuity Prana"
    "Keen Sight Technique"
    "Frugal Merchant Method"],
   :last-accessed 4535361,
   :weapon-inventory
   [{:name "Short Sword", :as "Katana", :category :mundane-weapons}
    {:name "Unarmed", :category :mundane-weapons}],
   :held-weapon
   {:name "Short Sword", :as "Katana", :category :mundane-weapons},
   :essence
   {:rating 1,
    :motes-spent-personal 4,
    :motes-spent-peripheral 2,
    :motes-committed-peripheral 2,
    :motes-committed-personal 0},
   :player "Nobody",
   :xp {:spent 55, :earned 60, :solar 0, :silver 3, :gold 2, :white 1},
   :health-module
   {:levels [4 1 4 5], :bashing 2, :lethal 1, :aggravated 3},
   :intimacies
   [{:type :principle,
     :severity :defining,
     :feeling "",
     :description "The Five Truths"}
    {:type :principle,
     :severity :major,
     :feeling "",
     :description "There is no pleasure like a bright pupil"}
    {:type :tie,
     :severity :major,
     :feeling "Respect and Fear",
     :description "The Scarlet Empress"}
    {:type :tie,
     :severity :minor,
     :feeling "Gratitude",
     :description "Amon the Demon's Head"}
    {:type :tie,
     :severity :minor,
     :feeling "Profound Discomfort",
     :description "The Owl Screaming In Torment"}],
   :favored-abilities
   [:awareness
    :brawl
    :bureaucracy
    :dodge
    :integrity
    :linguistics
    :lore
    :occult
    :socialize
    :martial-arts],
   :attributes
   {:charisma 3,
    :perception 2,
    :intelligence 5,
    :stamina 2,
    :strength 2,
    :dexterity 4,
    :wits 4,
    :appearance 3,
    :manipulation 3},
   :img "https://i.imgur.com/cFE1bow.jpg",
   :subtype :twilight,
   :merits
   [{:name "Ambidexterous", :rank 2, :note ""}
    {:name "Claws/Fangs/Hooves/Horns", :rank 4, :note "Claws"}
    {:name "Iron Stomach", :rank 1, :note "Once ate a boat"}
    {:name "Language", :rank 1, :note "Riverspeak"}
    {:name "Language", :rank 1, :note "High Realm"}]})


(defn new-thing
  "Makes and returns a new thing of the provided category. If the category is not supported (or :player when include-player? is falsey), returns false."
  [category name key include-player?]
  (case (keyword category)
    :character (character name key)
    :player nil))