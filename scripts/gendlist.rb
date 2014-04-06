#!/usr/bin/env ruby

ds = Dir.glob("wav_files/*.wav")
File.open("DataList.txt", "w") do |file|

  ds.each do |name|
    case name
    when %r(F L.wav$)
      m = name.match(%r(wav_files/(.*) F L.wav))
      file.write("0, FL, #{m[1]},,bin/dev/#{name},,5.0\n")
    when %r(M L.wav$)
      m = name.match(%r(wav_files/(.*) M L.wav))
      file.write("0, ML, #{m[1]},,bin/dev/#{name},,5.0\n")
    when %r(M.wav$)
      m = name.match(%r(wav_files/(.*) M.wav))
      file.write("0, M, #{m[1]},,bin/dev/#{name},,0.0\n")
    when %r(F.wav$)
      m = name.match(%r(wav_files/(.*) F.wav))
      file.write("0, F, #{m[1]},,bin/dev/#{name},,0.0\n")
    end
  end
end