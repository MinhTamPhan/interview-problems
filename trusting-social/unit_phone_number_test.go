package main

import (
	"os"
	"reflect"
	"testing"
	"time"
)

func Test_read(t *testing.T) {
	f1, err := os.Open("testcase/wrong.csv")
	if err != nil {
		panic(err)
	}
	f2, err := os.Open("input.csv")
	if err != nil {
		panic(err)
	}
	mapActivities := map[int][]*Pair{
		987000001: {&Pair{
			First:  time.Date(2016, 3, 1, 0, 0, 0, 0, time.UTC).Unix(),
			Second: time.Date(2016, 5, 1, 0, 0, 0, 0, time.UTC).Unix(),
		}, &Pair{
			First:  time.Date(2016, 1, 1, 0, 0, 0, 0, time.UTC).Unix(),
			Second: time.Date(2016, 3, 1, 0, 0, 0, 0, time.UTC).Unix(),
		}, &Pair{
			First:  time.Date(2016, 12, 1, 0, 0, 0, 0, time.UTC).Unix(),
			Second: 0,
		}, &Pair{
			First:  time.Date(2016, 9, 1, 0, 0, 0, 0, time.UTC).Unix(),
			Second: time.Date(2016, 12, 1, 0, 0, 0, 0, time.UTC).Unix(),
		}, &Pair{
			First:  time.Date(2016, 6, 1, 0, 0, 0, 0, time.UTC).Unix(),
			Second: time.Date(2016, 9, 1, 0, 0, 0, 0, time.UTC).Unix(),
		}},
		987000002: {&Pair{
			First:  time.Date(2016, 2, 1, 0, 0, 0, 0, time.UTC).Unix(),
			Second: time.Date(2016, 3, 1, 0, 0, 0, 0, time.UTC).Unix(),
		}, &Pair{
			First:  time.Date(2016, 3, 1, 0, 0, 0, 0, time.UTC).Unix(),
			Second: time.Date(2016, 5, 1, 0, 0, 0, 0, time.UTC).Unix(),
		}, &Pair{
			First: time.Date(2016, 5, 1, 0, 0, 0, 0, time.UTC).Unix(),
		}},
		987000003: {&Pair{
			First:  time.Date(2016, 1, 1, 0, 0, 0, 0, time.UTC).Unix(),
			Second: time.Date(2016, 1, 10, 0, 0, 0, 0, time.UTC).Unix(),
		}},
	}
	type args struct {
		input *os.File
	}
	tests := []struct {
		name    string
		args    args
		want    map[int][]*Pair
		wantErr bool
	}{
		{
			name: "TC0 input null",
			args: args{
				input: nil,
			},
			want:    nil,
			wantErr: true,
		},
		{
			name: "TC1 read file wrong format",
			args: args{
				input: f1,
			},
			want:    nil,
			wantErr: true,
		},
		{
			name: "TC2 read file success",
			args: args{
				input: f2,
			},
			want:    mapActivities,
			wantErr: false,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, err := read(tt.args.input)
			if (err != nil) != tt.wantErr {
				t.Errorf("read() error = %v, wantErr %v", err, tt.wantErr)
				return
			}
			if !reflect.DeepEqual(got, tt.want) {
				t.Errorf("read() = %v, want %v", got, tt.want)
			}
		})
	}
}

func TestUnitPhoneNumberImp(t *testing.T) {
	f1, err := os.Open("testcase/wrong.csv")
	if err != nil {
		panic(err)
	}
	f2, err := os.Open("input.csv")
	if err != nil {
		panic(err)
	}
	type args struct {
		numWorker int
		intput    *os.File
		output    *os.File
	}
	tests := []struct {
		name    string
		args    args
		wantErr bool
	}{
		{
			name: "TC0 nil input, output",
			args: args{
				numWorker: 0,
				intput:    nil,
				output:    nil,
			},
			wantErr: true,
		},
		{
			name: "TC1 invalid input",
			args: args{
				numWorker: 3,
				intput:    f1,
				output:    nil,
			},
			wantErr: true,
		},
		{
			name: "TC3 success",
			args: args{
				numWorker: 0,
				intput:    f2,
				output:    f1,
			},
			wantErr: false,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if err := UnitPhoneNumberImp(tt.args.numWorker, tt.args.intput, tt.args.output); (err != nil) != tt.wantErr {
				t.Errorf("UnitPhoneNumberImp() error = %v, wantErr %v", err, tt.wantErr)
			}
		})
	}
}
