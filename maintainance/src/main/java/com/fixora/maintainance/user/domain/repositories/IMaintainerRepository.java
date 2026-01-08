package com.fixora.maintainance.user.domain.repositories;

import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.model.User;

public interface IMaintainerRepository {

    Maintainer addMaintainer(User user);
}
