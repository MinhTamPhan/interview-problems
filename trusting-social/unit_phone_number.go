package main

import (
	"bufio"
	"fmt"
	"os"
	"sort"
	"strconv"
	"strings"
	"sync"
	"time"
)

type Pair struct {
	First, Second int64
}

type Record struct {
	phone      int64
	activities []*Pair
}

func read(input *os.File) map[int][]*Pair {
	reader := bufio.NewReader(input)
	res := make(map[int][]*Pair)
	reader.ReadString('\n')
	for {
		text, ok := reader.ReadString('\n')
		if ok != nil {
			break
		}
		text = strings.TrimSuffix(text, "\n")
		s := strings.Split(text, ",")
		phone, _ := strconv.Atoi(s[0])
		activate, _ := time.Parse("2006-01-02", s[1])
		deactivate, err := time.Parse("2006-01-02", s[2])
		if err != nil {
			deactivate = time.Unix(0, 0)
		}
		res[phone] = append(res[phone], &Pair{
			First:  activate.Unix(),
			Second: deactivate.Unix(),
		})
	}
	return res
}

func processPhoneActivities(in chan *Record, wg *sync.WaitGroup, output ...*os.File) {
	writer := bufio.NewWriter(os.Stdout)
	if output != nil && len(output) == 1 {
		writer = bufio.NewWriter(output[0])
	}
	for {
		record, ok := <-in
		if !ok {
			break
		}
		activities := record.activities
		sort.SliceStable(activities, func(i, j int) bool {
			return activities[i].First < activities[j].First
		})
		n := len(activities)
		activateDate := activities[n-1].First
		for i := n - 2; i >= 0; i-- {
			acti := activities[i].First
			deacti := activities[i].Second
			if activateDate == deacti {
				activateDate = acti
			} else {
				break
			}
		}
		fmt.Println(writer.WriteString("123321"))
	}
	wg.Done()
}

// exported method
func UnitPhoneNumberImp(numWorker int, intput *os.File, output *os.File) {

	var wg sync.WaitGroup
	activitiesLog := read(intput)
	channels := make([]chan *Record, numWorker)
	for i := range channels {
		channels[i] = make(chan *Record, 5)
	}
	count := 0
	for phone, activities := range activitiesLog {
		channels[count%numWorker] <- &Record{
			phone:      int64(phone),
			activities: activities,
		}
		count++
	}

	for i := 0; i < numWorker; i++ {
		wg.Add(1)
		go processPhoneActivities(channels[i], &wg, nil)
	}
	for i := range channels {
		close(channels[i])
	}
	wg.Wait()
}

func main() {
	UnitPhoneNumberImp(1, os.Stdin, os.Stdout)
}
