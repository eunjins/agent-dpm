package kr.co.dpm.agent.device.repository;

import kr.co.dpm.model.Measure;

public interface MeasureRepository {
    public boolean request(Measure measure);
}
