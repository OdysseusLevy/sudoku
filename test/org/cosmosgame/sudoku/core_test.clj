(ns org.cosmosgame.sudoku.core-test
  (:use clojure.test
        org.cosmosgame.sudoku.core))

;
; Tests
;

(deftest test-get-row-index
   (is (= '(0 1 4 8) (map get-row-index [8 9 40 80]) )))

(deftest test-get-row
  (is (= (range 9 18) (get-row 11)))
  (is (= (range 0 9) (get-row 1))))

(deftest test-get-col-index
  (let [input (take 9 (iterate #(+ 10 %) 0))]
    (is (= (range 0 9) (map get-col-index input)))))

(deftest test-get-col
    (is (= (range 0 81 9 ) (get-col 18))))

; test remove-possibilities

(deftest test-remove-possibilities
  (testing "test remove-possibilities"
    (is (= '#{[#{1 3} 5] [#{1 6} 10]}), (remove-possibilities #{[#{4 8} 4] [#{1 6} 10] [#{1 3 8} 5] } 4 8) )))

(deftest test-valid-move?
  (let [board (create-board )
        board2 (add-move board 0 2)]
    (is (= false (valid-move? board [1 10])))
    (is (= true (valid-move? board [1 9])))
    (is (= true  (valid-move? board2 [1 7])))
    (is (= false (valid-move? board2 [1 2])))))

; test add-move

;(deftest test-add-move
;  (let [simple-board {:remaining #{[#{4 8} 2] [#{1 8} 4] [#{6 8} 9]} :answers (vec (concat ))}]
;    (is (= {:remaining #{[#{4} 2] [#{6 8} 9]} :answers [2 2 0 0 8 0 0 0 6 0 0 0 0]}
;          (add-move simple-board 4 8 )))))

(deftest test-check-add-move
  (let [board (create-board)
        result-good (check-add-move board [3 8])
        result-bad (check-add-move result-good [2 8])]
    (is (= true (:error result-bad)))
    (is (= nil (:error result-good)))))

(deftest test-read-board
  (testing "bad board"
    (let [bad-board-str "999000000000000000000008000045000000003000000006003054000325006000000000000000000"
          bad-board (read-board bad-board-str)]
      (is (= true (:error bad-board)))))
  (testing "good board"
    (let [good-board-str "200000060000075030048090100000300000300010009000008000001020570080730000090000004"
          good-board (read-board good-board-str)]
      (is (= nil (:error good-board))))))
;
; Test solving some boards
;

;
; Setup board test data
;

(def test-data "200000060000075030048090100000300000300010009000008000001020570080730000090000004")
(def test-board (read-board test-data))

;http://norvig.com/sudoku.html
(def test-data2 "400000805030000000000700000020000060000080400000010000000603070500200000104000000")
(def test-board2 (read-board test-data2))

;http://www.telegraph.co.uk/science/science-news/9359579/Worlds-hardest-sudoku-can-you-crack-it.html
(def test-hard1 "800000000003600000070090200050007000000045700000100030001000068008500010090000400")
(def test-board-hard1 (read-board test-hard1))

;http://www.mirror.co.uk/news/weird-news/worlds-hardest-sudoku-can-you-242294
(def test-inkala "005300000800000020070010500400005300010070006003200080060500009004000030000009700")
(def test-board-inkala (read-board test-inkala))

;http://norvig.com/sudoku.html
; This one takes a long time
(def test-hard2 "000006000059000008200008000045000000003000000006003054000325006000000000000000000")
(def test-board-hard2 (read-board test-hard2))


(deftest test-solve

  (testing "simple board"
    (is (=[2 7 3 4 8 1 9 6 5 9 1 6 2 7 5 4 3 8 5 4 8 6 9 3 1 2 7 8 5 9 3 4 7 6 1 2 3 6 7 5 1 2 8 4 9 1 2 4 9 6 8 7 5 3 4 3 1 8 2 9 5 7 6 6 8 5 7 3 4 2 9 1 7 9 2 1 5 6 3 8 4]
        (solve test-board))))

  (testing "norvig example 1"
    (is (= [4 1 7 3 6 9 8 2 5 6 3 2 1 5 8 9 4 7 9 5 8 7 2 4 3 1 6 8 2 5 4 3 7 1 6 9 7 9 1 5 8 6 4 3 2 3 4 6 9 1 2 7 5 8 2 8 9 6 4 3 5 7 1 5 7 3 2 9 1 6 8 4 1 6 4 8 7 5 2 9 3]
          (solve test-board2)))))


