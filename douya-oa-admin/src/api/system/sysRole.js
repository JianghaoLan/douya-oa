import request from '@/utils/request'

const apiName = '/admin/system/sysRole'

export default {
  getPageList(current, limit, searchParams) {
    return request({
      url: `${apiName}/${current}/${limit}`,
      method: 'get',
      params: searchParams
    })
  },
  removeById(id) {
    return request({
      url: `${apiName}/remove/${id}`,
      method: 'delete'
    })
  },
  saveRole(role) {
    return request({
      url: `${apiName}/save`,
      method: 'post',
      data: role
    })
  },
  getById(id) {
    return request({
      url: `${apiName}/get/${id}`,
      method: 'get'
    })
  },
  
  updateById(role) {
    return request({
      url: `${apiName}/update`,
      method: 'put',
      data: role
    })
  },
  batchRemove(ids) {
    return request({
      url: `${apiName}/batchRemove`,
      method: 'delete',
      data: ids
    })
  },
  getRoles(adminId) {
    return request({
      url: `${apiName}/toAssign/${adminId}`,
      method: 'get'
    })
  },
  assignRoles(assginRoleVo) {
    return request({
      url: `${apiName}/doAssign`,
      method: 'post',
      data: assginRoleVo
    })
  }
}
