#!/usr/bin/env ruby

ds = Dir.glob("wav_files/**/*.wav")
count = {}

File.open("DataList.txt", "w") do |file|
  ds.each do |name|
    case name
    when %r(M L.wav$)
      m = name.match(%r(wav_files/(.*)/(.*)/(.*)/(.*) M L.wav))
      count[m[1]] ||= {}
      count[m[1]][m[2]] ||= 0
      count[m[1]][m[2]] += 1
      # next if count[m[1]][m[2]] > 1
      file.write("0, #{m[1]}, #{m[4]},,Instructions/AUDIO/#{name},,0.0\n")
    end
  end
end


puts count