using System;
using System.Linq;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Globalization;
using System.Text.RegularExpressions;

class Solution {
	static int rank = 3; //this value shall be modified

	static void Main(string[] args) {
		//regex to find numbers in the parameter file - you may just do a string split depending on how your file looks like
		Regex regex = new Regex (@"(?<![\w_\-\d])-?\d+(\.?\d*)?([eE]\-?\d+)?");
		//is a parameter file passed via command line argument?
		if (args.Length == 1) {
			//read all lines from the file
			string[] lines = File.ReadAllLines (args [0]);
			foreach (string line in lines) {
				if (!regex.IsMatch (line))
					continue; //no number found
				double value = double.Parse (regex.Matches (line) [0].ToString(), CultureInfo.InvariantCulture);
				//if the line contains "rank", store the value in the rank variable
				if (line.Contains ("rank")) {
					rank = (int)value;
				}
			}
		}
		
		// here goes the c4l bot
		// not relevant for the fiddler, just to have a working example
		string[] inputs;
		int projectCount = int.Parse (Console.ReadLine ());
		for (int i = 0; i < projectCount; i++) {
			Console.ReadLine ();
		}

		// game loop
		while (true)
		{
			Player me = null;
			for (int i = 0; i < 2; i++) {
				inputs = Console.ReadLine ().Split (' ');
				string target = inputs [0];
				int eta = int.Parse (inputs [1]);
				int score = int.Parse (inputs [2]);
				if (i == 0)
					me = new Player (target, eta, score, 
						Enumerable.Range (3, 5).Select (storage => int.Parse (inputs [storage])).ToArray (),
						Enumerable.Range (8, 5).Select (expertise => int.Parse (inputs [expertise])).ToArray ());
			}

			int[] available = Console.ReadLine ().Split (' ').Select (int.Parse).ToArray ();
			int sampleCount = int.Parse (Console.ReadLine ());
			List<Sample> samples = new List<Sample> ();
			for (int i = 0; i < sampleCount; i++) {
				inputs = Console.ReadLine ().Split (' ');
				int sampleId = int.Parse (inputs [0]);
				int carriedBy = int.Parse (inputs [1]);
				int rank = int.Parse (inputs [2]);
				string expertiseGain = inputs [3];
				int health = int.Parse (inputs [4]);
				samples.Add (new Sample (sampleId, carriedBy, rank, expertiseGain [0] - 'A', health, 
					Enumerable.Range (5, 5).Select (cost => int.Parse (inputs [cost])).ToArray ()));
			}
			me.Samples = samples.Where (s => s.CarriedBy == 0).ToList ();
			Console.WriteLine (me.Move(available));
		}
	}

	class Player {
		public string Target;
		public int ETA;
		public int Score;
		public int[] Storage;
		public int[] InitialStorage;
		public int[] Expertise;
		public List<Sample> Samples = new List<Sample>();

		public Player(string target, int eta, int score, int[] storage, int[] expertise) {
			this.Target = target;
			this.ETA = eta;
			this.Score = score;
			this.Storage = storage;
			this.InitialStorage = (int[])storage.Clone();
			this.Expertise = expertise;
		}

		public string Move(int[] available) {
			int expert = this.Expertise.Sum ();
			int free = 10 - this.Storage.Sum ();

			switch (Target) {
			case "SAMPLES":
				if (Samples.Count () == 3) {
					return "GOTO DIAGNOSIS";
				}
				return "CONNECT " + (expert < rank ? 1 : 2);
			case "DIAGNOSIS":
				foreach (Sample s in Samples.Where(sample => !sample.Diagnosed)) {
					return "CONNECT " + s.ID;
				}
				return "GOTO MOLECULES";
			case "MOLECULES":
				foreach (Sample s in Samples) {
					if (!s.CanFill (Expertise, Storage, free, available)) {
						continue;
					}
					for (int i = 0; i < Storage.Length; i++) {
						if (s.Cost [i] > Storage [i] + Expertise [i]) {
							return "CONNECT " + (char)('A' + i);
						}
						Storage [i] -= Math.Max (0, s.Cost [i] - Expertise [i]);
					}
				}
				return "GOTO LABORATORY";
			case "LABORATORY":
				foreach (Sample s in Samples) {
					if (s.CalcCost(Expertise, Storage) == 0) {
						return "CONNECT " + s.ID;
					}
				}
				return "GOTO SAMPLES";
			default:
				return "GOTO SAMPLES"; //from START_POS
			}
		}
	}

	class Sample {
		public int ID;
		public int CarriedBy;
		public int Rank;
		public int ExpertiseGain;
		public int Health;
		public int[] Cost;
		public bool Diagnosed;

		public Sample(int id, int carriedBy, int rank, int expertiseGain, int health, int[] cost) {
			this.ID = id;
			this.CarriedBy = carriedBy;
			this.Rank = rank;
			this.ExpertiseGain = expertiseGain;
			this.Health = health;
			this.Diagnosed = health > 0;
			if (this.Diagnosed) this.Cost = cost;
		}

		public int CalcCost(int[] expertise, int[] storage) {
			int result = 0;
			for (int i = 0; i < Cost.Length; i++) {
				result += Math.Max (0, Cost [i] - expertise [i] - storage [i]);
			}
			return result;
		}

		public bool CanFill(int[] expertise, int[] storage, int free, int[] available) {
			if (free < CalcCost (expertise, storage)) {
				return false;
			}

			for (int i = 0; i < Cost.Length; i++) {
				if (Cost [i] > expertise [i] + storage [i] + available [i]) {
					return false;
				}
			}
			return true;
		}
	}
}
