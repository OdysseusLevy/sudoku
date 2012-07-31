(ns org.cosmosgame.sudoku.core (:gen-class)
  (:use org.cosmosgame.sudoku.core clojure.tools.logging))

;; Utilities
(defn print-moves
  ([v]
    (if (empty? v)
      nil
      (do
        (println (take 9 v))
        (print-moves (nthrest v 9))))))

; remove?
(defn get-position
  [ [col row]]
  (+ (* row 9) col))

;
; START

(defn udiv
  [x y]
  (unchecked-divide-int x y))

(defn round
  [x multiple]
  (* (udiv x multiple) multiple))

(defn get-row-index
  ([n]
    (udiv n 9)))

(defn get-row
  [n]
  (let [start (round n 9)
        end (+ start 9)]
    (range start end)))

(defn get-col-index
  ([n]
    (rem n 9)))

(defn get-col
  [n]
    (let [start (get-col-index n)]
    (range start 81 9)))

(defn get-square-index
  [n]
  (let [row (round (get-row-index n) 3)
        col (round (get-col-index n) 3)]
     [col row]))

(defn get-square
  [n]
    (let [pos (get-position (get-square-index n))
        row1 (range pos (+ pos 3 ))
        row2 (range (+ pos 9) (+ pos 12))
        row3 (range (+ pos 18) (+ pos 21))]
    (concat row1 row2 row3)))

(defn get-related
  [n]
  (into #{} (concat (get-col n) (get-row n) (get-square n))))

(defn is-related
  [n candidate]
  (if (= (get-row-index n) (get-row-index candidate))
    true
    (if (= (get-col-index n) (get-col-index candidate))
      true
      (if (= (get-square-index n) (get-square-index candidate))
        true
        false))))

(def is-related-memo (memoize is-related))

(defn compare-possibility
  [ [possibilities1 pos1 :as v1]  [possibilities2 pos2 :as v2] ]

  (let [diff (- (count possibilities1) (count possibilities2)) ]
    (if (zero? diff)
      (compare pos1 pos2)
      diff)))

(defn remove-possibility
  [position value set]

  (let [ [possibles pos] set]
    (if (= position pos)
      nil
      (if (is-related position pos)
        [(disj possibles value) pos]
        set))))

(defn remove-possibilities
  [remaining position value]
  ( let [ remaining (map #(remove-possibility position value %) remaining)
          remaining (into (sorted-set-by compare-possibility) remaining)]
        (disj remaining nil)))

(defn add-move
  ([board position value]
    (if (zero? value)
      board
      (let [board (assoc board :remaining (remove-possibilities  (:remaining board) position value))
           board (assoc-in board [:answers position] value)]
        board)))

  ([board [position value]]
    (add-move board position value)))

(defn create-possibility
  [n]
  [ (set (range 1 10)) n] )

(defn create-board
  []
  (let [ remaining (map create-possibility (range 0 81))
         remaining (into (sorted-set-by compare-possibility) remaining) ]
    { :remaining remaining :answers (vec (repeat 81 0))}))

(defn read-move
  [i s]
  (vector i (read-string s)))

(defn read-moves
  [s]
  (let [strings (map str s)]
    (map-indexed read-move strings)))

(defn valid-value?
  [num]
  (if (and (<= num 9) (>= num 0))
    true
    false))

(defn valid-move?
  [board [position value]]
  (if (zero? value)
    true
    (if (not (valid-value? value))
      false
      (let [related (get-related position)
            related (disj related position)
            values (into #{} (map (:answers board) related))]
            (not (contains? values value))))))

(defn check-add-move
  [board move]
  (if (not (valid-move? board move))
    (assoc board :error true)
    (add-move board move)))

(defn read-board
  [s]
  (let [board (create-board)
        moves (read-moves s)]
        (reduce check-add-move board moves)))

(defn next-moves
  [board]
  (let [head (first (:remaining board))
        [values pos] head]
    (map #(vector pos %) values)))

(defn solved?
  [{remaining :remaining answers :answers} ]
    (if (some zero? answers)
      false
      true))

(defn error?
  [board]
  (true? (or (nil? board) (:error board))))

(defn solve
  [board]
  (debug "solve" board)
  (if (error? board)
    nil
    (if (solved? board)
      (:answers board)
      (let [ moves (next-moves board)
             boards (map #(add-move board %) moves)]
        (some solve boards)))))

(defn -main
  "Sudoku solver"
  [board-str]
  (info "solving: " board-str)
  (let [board (read-board board-str)
        answer (solve board)]
    (if (nil? answer)
      (println (vec (repeat 81 0)))
      (println answer))))
